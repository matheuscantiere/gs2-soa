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
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Limite de tentativas de login, limite de reportes por IP, duplicata sem cruzamento e remoção de dado pessoal.
 * Cada teste usa um IP próprio porque o contador fica em memória (não é desfeito pelo rollback).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProtecaoAbusoTest {
    @Autowired
    MockMvc mockMvc;

    private static RequestPostProcessor ip(String endereco) {
        return request -> {
            request.setRemoteAddr(endereco);
            return request;
        };
    }

    private int login(String endereco, String senha) throws Exception {
        return mockMvc.perform(post("/login").with(ip(endereco)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin\",\"senha\":\"" + senha + "\"}"))
                .andReturn().getResponse().getStatus();
    }

    @Test
    @DisplayName("Login: 5 senhas erradas bloqueiam o par IP+login (429), até com a senha certa; outro IP segue livre")
    void bloqueioLogin() throws Exception {
        for (int i = 0; i < 5; i++) {
            assertThat(login("10.0.0.1", "errada")).isEqualTo(401);
        }
        mockMvc.perform(post("/login").with(ip("10.0.0.1")).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin\",\"senha\":\"admin123\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.erro.codigo").value("muitas_tentativas"));
        assertThat(login("10.0.0.99", "admin123")).isEqualTo(200);
    }

    @Test
    @DisplayName("Login: acertar a senha zera a contagem de erros")
    void loginCertoZeraContagem() throws Exception {
        for (int i = 0; i < 4; i++) {
            assertThat(login("10.0.0.2", "errada")).isEqualTo(401);
        }
        assertThat(login("10.0.0.2", "admin123")).isEqualTo(200);
        for (int i = 0; i < 4; i++) {
            assertThat(login("10.0.0.2", "errada")).isEqualTo(401);
        }
    }

    @Test
    @DisplayName("Reportes: a partir da 31ª requisição no minuto o mesmo IP recebe 429 (inválidas também contam)")
    void limiteReportes() throws Exception {
        for (int i = 0; i < 30; i++) {
            mockMvc.perform(post("/reportes").with(ip("10.0.0.3")).contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isBadRequest());
        }
        mockMvc.perform(post("/reportes").with(ip("10.0.0.3")).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"))
                .andExpect(jsonPath("$.erro.codigo").value("muitas_tentativas"));
        mockMvc.perform(post("/reportes").with(ip("10.0.0.4")).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Reportes sem cruzamento: mesmo ponto (~110 m) em 5 min é duplicata; ponto distante não é")
    void duplicataPorCoordenada() throws Exception {
        String ponto = "{\"origem\":\"APP\",\"latitude\":%s,\"longitude\":-46.6500}";
        mockMvc.perform(post("/reportes").with(ip("10.0.0.5")).contentType(MediaType.APPLICATION_JSON)
                        .content(ponto.formatted("-23.6000")))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/reportes").with(ip("10.0.0.5")).contentType(MediaType.APPLICATION_JSON)
                        .content(ponto.formatted("-23.6005")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro.codigo").value("reporte_duplicado"));
        mockMvc.perform(post("/reportes").with(ip("10.0.0.5")).contentType(MediaType.APPLICATION_JSON)
                        .content(ponto.formatted("-23.6100")))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Dado pessoal na descrição é removido antes de gravar e a resposta avisa (dados_removidos)")
    @WithMockUser(roles = "ADMIN")
    void removeDadoPessoal() throws Exception {
        String json = mockMvc.perform(post("/reportes").with(ip("10.0.0.6")).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"origem":"APP","latitude":-23.62,"longitude":-46.66,
                                 "descricao":"Rua alagada! Me liga (11) 98765-4321, CPF 123.456.789-09, placa ABC1D23, joao@email.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dados_removidos").value(true))
                .andReturn().getResponse().getContentAsString();
        String id = json.replaceAll(".*\"id\":(\\d+).*", "$1");

        String gravado = mockMvc.perform(get("/reportes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados_removidos").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        assertThat(gravado)
                .contains("Rua alagada!", "[removido]")
                .doesNotContain("98765", "123.456", "ABC1D23", "joao@");
    }

    @Test
    @DisplayName("Descrição sem dado pessoal fica igual e dados_removidos = false")
    void descricaoSemDadoPessoal() throws Exception {
        mockMvc.perform(post("/reportes").with(ip("10.0.0.7")).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origem\":\"APP\",\"latitude\":-23.63,\"longitude\":-46.67,"
                                + "\"descricao\":\"Água na altura do joelho às 18:48 perto do nº 1200\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Água na altura do joelho às 18:48 perto do nº 1200"))
                .andExpect(jsonPath("$.dados_removidos").value(false));
    }
}
