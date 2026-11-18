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
class CruzamentoControllerTest {
    private static final String NOVO = """
            {"id":"teste-cruzamento","nome":"R. Teste × Av. Teste","nome_curto":"Teste × Teste",
             "via":"R. Teste","bairro":"Bairro Teste","latitude":-23.6,"longitude":-46.7,"placeholder":false,"ordem":9}
            """;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Expectativa: lista pública com os 4 cruzamentos no formato do app, sem token.")
    void listarCenario1() throws Exception {
        mockMvc.perform(get("/cruzamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].id").value("nu-jaime"))
                .andExpect(jsonPath("$[0].nome_curto").value("Nações Unidas × Jaime de Oliveira Souza"))
                .andExpect(jsonPath("$[0].latitude").value(-23.6814075))
                .andExpect(jsonPath("$[0].longitude").value(-46.694591))
                .andExpect(jsonPath("$[1].placeholder").value(true))
                .andExpect(jsonPath("$[1].via").isEmpty());
    }

    @Test
    @DisplayName("Expectativa: cadastrar sem token retorna 401.")
    void cadastrarCenario1() throws Exception {
        mockMvc.perform(post("/cruzamentos").contentType(MediaType.APPLICATION_JSON).content(NOVO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Expectativa: perfil USER não pode cadastrar (403).")
    @WithMockUser(roles = "USER")
    void cadastrarCenario2() throws Exception {
        mockMvc.perform(post("/cruzamentos").contentType(MediaType.APPLICATION_JSON).content(NOVO))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro.codigo").value("acesso_negado"));
    }

    @Test
    @DisplayName("Expectativa: ADMIN cadastra (201 + Location) e id repetido dá 409.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario3() throws Exception {
        mockMvc.perform(post("/cruzamentos").contentType(MediaType.APPLICATION_JSON).content(NOVO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/cruzamentos/teste-cruzamento")))
                .andExpect(jsonPath("$.ativo").value(true));

        mockMvc.perform(post("/cruzamentos").contentType(MediaType.APPLICATION_JSON).content(NOVO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro.codigo").value("conflito"));
    }

    @Test
    @DisplayName("Expectativa: dados inválidos retornam 400 validacao com os campos em snake_case.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario4() throws Exception {
        mockMvc.perform(post("/cruzamentos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"ID INVÁLIDO\",\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro.codigo").value("validacao"))
                .andExpect(jsonPath("$.erro.campos[?(@.campo == 'nome_curto')]").exists());
    }

    @Test
    @DisplayName("Expectativa: atualizar ignora campos nulos e excluir é lógico (some da lista e do /risco).")
    @WithMockUser(roles = "ADMIN")
    void atualizarExcluirCenario1() throws Exception {
        mockMvc.perform(post("/cruzamentos").contentType(MediaType.APPLICATION_JSON).content(NOVO))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/cruzamentos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"teste-cruzamento\",\"bairro\":\"Novo Bairro\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bairro").value("Novo Bairro"))
                .andExpect(jsonPath("$.nome").value("R. Teste × Av. Teste"));

        mockMvc.perform(delete("/cruzamentos/teste-cruzamento"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/risco").param("local", "teste-cruzamento"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Expectativa: listagem administrativa é paginada.")
    @WithMockUser(roles = "USER")
    void listarAdminCenario1() throws Exception {
        mockMvc.perform(get("/cruzamentos/admin").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(4));
    }
}
