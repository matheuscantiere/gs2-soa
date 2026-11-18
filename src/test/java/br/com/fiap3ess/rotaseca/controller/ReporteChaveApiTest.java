package br.com.fiap3ess.rotaseca.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Com REPORTES_CHAVE_API definida: sensor e parceiro precisam do header X-Api-Key; o app do cidadão não. */
@SpringBootTest(properties = "rotaseca.reportes.chave-api=chave-de-teste")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReporteChaveApiTest {
    private static final String SENSOR = """
            {"origem":"SENSOR","latitude":-23.64,"longitude":-46.68,"nivel_cm":20}
            """;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Sensor sem chave ou com chave errada: 401 chave_api_invalida")
    void sensorSemChave() throws Exception {
        mockMvc.perform(post("/reportes").contentType(MediaType.APPLICATION_JSON).content(SENSOR))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro.codigo").value("chave_api_invalida"));
        mockMvc.perform(post("/reportes").header("X-Api-Key", "errada").contentType(MediaType.APPLICATION_JSON).content(SENSOR))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Sensor com a chave certa: 201")
    void sensorComChave() throws Exception {
        mockMvc.perform(post("/reportes").header("X-Api-Key", "chave-de-teste")
                        .contentType(MediaType.APPLICATION_JSON).content(SENSOR))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("App do cidadão continua sem precisar de chave")
    void appSemChave() throws Exception {
        mockMvc.perform(post("/reportes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origem\":\"APP\",\"latitude\":-23.65,\"longitude\":-46.69}"))
                .andExpect(status().isCreated());
    }
}
