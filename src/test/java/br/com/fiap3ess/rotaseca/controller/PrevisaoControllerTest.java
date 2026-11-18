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

/** Usa datas de 2030 para nunca tocar no replay de 16/01/2025. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(roles = "ADMIN")
class PrevisaoControllerTest {
    @Autowired
    MockMvc mockMvc;

    private void enviar(String hora, String estimativa, int statusEsperado) throws Exception {
        mockMvc.perform(put("/previsoes").contentType(MediaType.APPLICATION_JSON).content("""
                        {"fonte":"ML_TESTE","previsoes":[{"local_id":"nu-jaime","previsao_hora":"%s","estimativa":%s}]}
                        """.formatted(hora, estimativa)))
                .andExpect(status().is(statusEsperado));
    }

    @Test
    @DisplayName("Expectativa: estimativa acima do limiar deixa o cruzamento elevado naquela hora.")
    void gravarCenario1() throws Exception {
        enviar("2030-03-04T18:00:00-03:00", "0.02", 200);
        mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", "2030-03-04T18:59:00-03:00"))
                .andExpect(jsonPath("$.status").value("elevado"))
                .andExpect(jsonPath("$.estimativa").value(0.02));
    }

    @Test
    @DisplayName("Expectativa: estimativa igual ao limiar é normal (limite estrito).")
    void gravarCenario2() throws Exception {
        enviar("2030-03-04T19:00:00-03:00", "0.01083", 200);
        mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", "2030-03-04T19:10:00-03:00"))
                .andExpect(jsonPath("$.status").value("normal"));
    }

    @Test
    @DisplayName("Expectativa: reenviar a mesma hora atualiza a previsão (upsert).")
    void gravarCenario3() throws Exception {
        enviar("2030-03-04T20:00:00-03:00", "0.5", 200);
        enviar("2030-03-04T20:00:00-03:00", "0.001", 200);
        mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", "2030-03-04T20:00:00-03:00"))
                .andExpect(jsonPath("$.status").value("normal"))
                .andExpect(jsonPath("$.estimativa").value(0.001));
    }

    @Test
    @DisplayName("Expectativa: previsao_hora quebrada, estimativa > 1 e placeholder retornam 400.")
    void gravarCenario4() throws Exception {
        enviar("2030-03-04T18:30:00-03:00", "0.02", 400);
        enviar("2030-03-04T18:00:00-03:00", "1.5", 400);
        mockMvc.perform(put("/previsoes").contentType(MediaType.APPLICATION_JSON).content("""
                        {"fonte":"ML_TESTE","previsoes":[{"local_id":"cruzamento-2","previsao_hora":"2030-03-04T18:00:00-03:00","estimativa":0.2}]}
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Expectativa: perfil USER não grava previsões.")
    @WithMockUser(roles = "USER")
    void gravarCenario5() throws Exception {
        enviar("2030-03-04T18:00:00-03:00", "0.02", 403);
    }

    @Test
    @DisplayName("Expectativa: alagamento registrado pelo CGE confirma o risco e aparece no bbox.")
    void alagamentoCenario1() throws Exception {
        enviar("2030-03-04T18:00:00-03:00", "0.02", 200);
        mockMvc.perform(post("/alagamentos").contentType(MediaType.APPLICATION_JSON).content("""
                        {"local_id":"nu-jaime","registrado_em":"2030-03-04T18:20:00-03:00","intransitavel":true}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fonte").value("CGE"));

        mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", "2030-03-04T18:30:00-03:00"))
                .andExpect(jsonPath("$.confirmado_cge").value(true))
                .andExpect(jsonPath("$.confirmado_em").value("2030-03-04T18:20:00-03:00"));

        mockMvc.perform(get("/alagamentos")
                        .param("bbox", "-46.75,-23.75,-46.60,-23.60")
                        .param("desde", "2030-03-04T00:00:00-03:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Expectativa: bbox mal formado retorna 400.")
    void alagamentoCenario2() throws Exception {
        mockMvc.perform(get("/alagamentos").param("bbox", "1,2,3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.codigo").value("parametro_invalido"));
    }

    @Test
    @DisplayName("Expectativa: alagamento registrado pela Defesa Civil não bloqueia rota (só o CGE confirma).")
    void alagamentoCenario3() throws Exception {
        enviar("2030-03-05T18:00:00-03:00", "0.02", 200);
        mockMvc.perform(post("/alagamentos").contentType(MediaType.APPLICATION_JSON).content("""
                        {"local_id":"nu-jaime","registrado_em":"2030-03-05T18:10:00-03:00","fonte":"DEFESA_CIVIL"}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", "2030-03-05T18:30:00-03:00"))
                .andExpect(jsonPath("$.status").value("elevado"))
                .andExpect(jsonPath("$.confirmado_cge").value(false));
    }

    @Test
    @DisplayName("Expectativa: confirmação do CGE de outra hora não vaza para a previsão atual.")
    void alagamentoCenario4() throws Exception {
        mockMvc.perform(post("/alagamentos").contentType(MediaType.APPLICATION_JSON).content("""
                        {"local_id":"nu-jaime","registrado_em":"2030-03-06T17:50:00-03:00"}
                        """))
                .andExpect(status().isCreated());
        enviar("2030-03-06T18:00:00-03:00", "0.02", 200);

        mockMvc.perform(get("/risco").param("local", "nu-jaime").param("em", "2030-03-06T18:30:00-03:00"))
                .andExpect(jsonPath("$.confirmado_cge").value(false));
    }
}
