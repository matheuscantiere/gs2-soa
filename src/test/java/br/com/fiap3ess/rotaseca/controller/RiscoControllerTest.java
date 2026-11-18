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

/**
 * Contrato com o app mobile: as respostas precisam ser idênticas às do mock do app (src/api/mock.ts),
 * usando o replay real de 16/01/2025 gravado pela migração V9.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RiscoControllerTest {
    private static final String AS_18_00 = "2025-01-16T18:00:00-03:00";
    private static final String AS_18_30 = "2025-01-16T18:30:00-03:00";
    private static final String AS_18_48 = "2025-01-16T18:48:00-03:00";

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Expectativa: às 18:00 o nu-jaime está elevado, sem confirmação, JSON igual ao mock do app.")
    void riscoCenario1() throws Exception {
        String json = mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", AS_18_00))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(json).isEqualTo("{"
                + "\"local_id\":\"nu-jaime\","
                + "\"cruzamento\":\"Av. das Nações Unidas × R. Jaime de Oliveira Souza\","
                + "\"previsao_hora\":\"2025-01-16T18:00:00-03:00\","
                + "\"valida_ate\":\"2025-01-16T19:00:00-03:00\","
                + "\"estimativa\":null,"
                + "\"limiar\":0.01083,"
                + "\"status\":\"elevado\","
                + "\"confirmado_cge\":false,"
                + "\"confirmado_em\":null}");
    }

    @Test
    @DisplayName("Expectativa: às 18:30 continua elevado e ainda sem confirmação do CGE.")
    void riscoCenario2() throws Exception {
        mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", AS_18_30))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("elevado"))
                .andExpect(jsonPath("$.confirmado_cge").value(false));
    }

    @Test
    @DisplayName("Expectativa: às 18:48 o CGE confirma o alagamento (confirmado_em 18:48).")
    void riscoCenario3() throws Exception {
        mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", AS_18_48))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("elevado"))
                .andExpect(jsonPath("$.confirmado_cge").value(true))
                .andExpect(jsonPath("$.confirmado_em").value(AS_18_48));
    }

    @Test
    @DisplayName("Expectativa: cruzamento placeholder responde sem_estimativa com a hora cheia do instante.")
    void riscoCenario4() throws Exception {
        mockMvc.perform(get("/risco").param("local", "cruzamento-2").param("em", AS_18_30))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("sem_estimativa"))
                .andExpect(jsonPath("$.previsao_hora").value(AS_18_00))
                .andExpect(jsonPath("$.valida_ate").value("2025-01-16T19:00:00-03:00"));
    }

    @Test
    @DisplayName("Expectativa: fora da hora do aviso o nu-jaime fica sem_estimativa.")
    void riscoCenario5() throws Exception {
        mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", "2025-01-16T20:10:00-03:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("sem_estimativa"))
                .andExpect(jsonPath("$.previsao_hora").value("2025-01-16T20:00:00-03:00"))
                .andExpect(jsonPath("$.confirmado_cge").value(false));
    }

    @Test
    @DisplayName("Expectativa: retornar 404 local_nao_encontrado para cruzamento inexistente.")
    void riscoCenario6() throws Exception {
        mockMvc.perform(get("/risco").param("local", "xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro.codigo").value("local_nao_encontrado"))
                .andExpect(jsonPath("$.erro.mensagem").value("Cruzamento 'xyz' não existe."));
    }

    @Test
    @DisplayName("Expectativa: retornar 400 parametro_invalido para em fora do ISO 8601.")
    void riscoCenario7() throws Exception {
        mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", "ontem"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.codigo").value("parametro_invalido"));
    }

    @Test
    @DisplayName("Expectativa: retornar 400 parametro_invalido sem local nem locais.")
    void riscoCenario8() throws Exception {
        mockMvc.perform(get("/risco"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.codigo").value("parametro_invalido"));
    }

    @Test
    @DisplayName("Expectativa: lote devolve riscos encontrados e nao_encontrados.")
    void riscoCenario9() throws Exception {
        mockMvc.perform(get("/risco").param("locais", "nu-jaime,cruzamento-2,xyz").param("em", AS_18_48))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riscos.length()").value(2))
                .andExpect(jsonPath("$.riscos[0].confirmado_cge").value(true))
                .andExpect(jsonPath("$.riscos[1].status").value("sem_estimativa"))
                .andExpect(jsonPath("$.nao_encontrados[0]").value("xyz"));
    }

    @Test
    @DisplayName("Expectativa: lote vazio retorna 400.")
    void riscoCenario10() throws Exception {
        mockMvc.perform(get("/risco").param("locais", " , "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.codigo").value("parametro_invalido"));
    }

    @Test
    @DisplayName("Expectativa: nenhum horário sai em UTC (Z); todos com -03:00, mesmo pedindo em UTC.")
    void riscoCenario11() throws Exception {
        String json = mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", "2025-01-16T21:48:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmado_em").value(AS_18_48))
                .andReturn().getResponse().getContentAsString();
        assertThat(json).doesNotContain("Z\"").contains("-03:00");
    }

    @Test
    @DisplayName("Expectativa: alertas do nu-jaime trazem o aviso das 18:00 já confirmado.")
    void alertasCenario1() throws Exception {
        mockMvc.perform(get("/alertas").param("local", "nu-jaime").param("desde", "2025-01-01T00:00:00-03:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].previsao_hora").value(AS_18_00))
                .andExpect(jsonPath("$[0].confirmado_cge").value(true));
    }

    @Test
    @DisplayName("Expectativa: risco por área (bbox) traz só os cruzamentos com coordenada dentro dela.")
    void riscoAreaCenario1() throws Exception {
        mockMvc.perform(get("/risco").param("bbox", "-46.75,-23.75,-46.60,-23.60").param("em", AS_18_48))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].local_id").value("nu-jaime"))
                .andExpect(jsonPath("$[0].confirmado_cge").value(true));

        mockMvc.perform(get("/risco").param("bbox", "-46.50,-23.50,-46.40,-23.40"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Expectativa: bbox inválido no /risco retorna 400.")
    void riscoAreaCenario2() throws Exception {
        mockMvc.perform(get("/risco").param("bbox", "-46.60,-23.60,-46.75,-23.75"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.codigo").value("parametro_invalido"));
    }
}
