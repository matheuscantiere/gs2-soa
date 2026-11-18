package br.com.fiap3ess.rotaseca.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReporteControllerTest {
    private static final String SENSOR = """
            {"origem":"SENSOR","local_id":"nu-jaime","latitude":-23.6814075,"longitude":-46.694591,"nivel_cm":22}
            """;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Expectativa: retornar código 400 para corpo vazio.")
    void receberCenario1() throws Exception {
        mockMvc.perform(post("/reportes"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Expectativa: sensor sem token é aceito (201) e 22 cm vira intransitavel_a_pe.")
    void receberCenario2() throws Exception {
        mockMvc.perform(post("/reportes").contentType(MediaType.APPLICATION_JSON).content(SENSOR))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.faixa").value("intransitavel_a_pe"))
                .andExpect(jsonPath("$.recebido_em").value(org.hamcrest.Matchers.endsWith("-03:00")));
    }

    @Test
    @DisplayName("Expectativa: sensor sem nivel_cm retorna 400.")
    void receberCenario3() throws Exception {
        mockMvc.perform(post("/reportes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origem\":\"SENSOR\",\"latitude\":-23.68,\"longitude\":-46.69}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.codigo").value("validacao"));
    }

    @Test
    @DisplayName("Expectativa: coordenada fora da Grande SP retorna 400.")
    void receberCenario4() throws Exception {
        mockMvc.perform(post("/reportes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origem\":\"APP\",\"latitude\":-22.9,\"longitude\":-43.2}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Expectativa: mesmo sensor no mesmo cruzamento em menos de 5 min retorna 409.")
    void receberCenario5() throws Exception {
        mockMvc.perform(post("/reportes").contentType(MediaType.APPLICATION_JSON).content(SENSOR))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/reportes").contentType(MediaType.APPLICATION_JSON).content(SENSOR))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro.codigo").value("reporte_duplicado"));
    }

    @Test
    @DisplayName("Expectativa: cruzamento inexistente no reporte retorna 404.")
    void receberCenario6() throws Exception {
        mockMvc.perform(post("/reportes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origem\":\"APP\",\"local_id\":\"xyz\",\"latitude\":-23.68,\"longitude\":-46.69}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro.codigo").value("local_nao_encontrado"));
    }

    @Test
    @DisplayName("Expectativa: listagem exige token; ADMIN recebe página.")
    @WithMockUser(roles = "ADMIN")
    void listarCenario1() throws Exception {
        mockMvc.perform(post("/reportes").contentType(MediaType.APPLICATION_JSON).content(SENSOR))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].origem").value("SENSOR"));
    }

    @Test
    @DisplayName("Expectativa: listagem sem token retorna 401 no formato de erro do contrato.")
    void listarCenario2() throws Exception {
        mockMvc.perform(get("/reportes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro.codigo").value("nao_autenticado"));
    }
}
