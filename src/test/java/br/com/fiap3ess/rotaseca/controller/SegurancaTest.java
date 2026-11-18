package br.com.fiap3ess.rotaseca.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Segurança de ponta a ponta com tokens reais (sem @WithMockUser): rotas protegidas, perfis,
 * expiração, assinatura, emissor e algoritmo do JWT.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SegurancaTest {
    @Autowired
    MockMvc mockMvc;

    @Value("${api.security.token.secret}")
    String secret;

    private String login(String usuario, String senha) throws Exception {
        String json = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"" + usuario + "\",\"senha\":\"" + senha + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    @ParameterizedTest(name = "{0} {1} sem token -> 401")
    @CsvSource({
            "GET, /cruzamentos/admin",
            "POST, /cruzamentos",
            "PUT, /cruzamentos",
            "DELETE, /cruzamentos/nu-jaime",
            "PUT, /previsoes",
            "POST, /alagamentos",
            "GET, /reportes",
            "GET, /reportes/1"
    })
    @DisplayName("Endpoints administrativos exigem token")
    void rotasProtegidas(String metodo, String rota) throws Exception {
        mockMvc.perform(request(HttpMethod.valueOf(metodo), rota).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro.codigo").value("nao_autenticado"));
    }

    @ParameterizedTest(name = "GET {0} sem token -> {1}")
    @CsvSource({
            "/cruzamentos, 200",
            "/cruzamentos/nu-jaime, 200",
            "/risco?local=nu-jaime, 200",
            "/alertas?local=nu-jaime, 200",
            "'/alagamentos?bbox=-46.75,-23.75,-46.60,-23.60', 200",
            "/health, 200",
            "/v3/api-docs, 200"
    })
    @DisplayName("Endpoints do app mobile e documentação são públicos")
    void rotasPublicas(String rota, int esperado) throws Exception {
        mockMvc.perform(get(rota)).andExpect(status().is(esperado));
    }

    @Test
    @DisplayName("Perfil USER (login real) lista, mas não grava previsões nem cadastra cruzamentos")
    void perfilUser() throws Exception {
        String token = login("consulta", "consulta123");
        mockMvc.perform(get("/cruzamentos/admin").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(put("/previsoes").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fonte\":\"X\",\"previsoes\":[{\"local_id\":\"nu-jaime\",\"previsao_hora\":\"2030-01-01T10:00:00-03:00\",\"estimativa\":0.5}]}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro.codigo").value("acesso_negado"));
        mockMvc.perform(get("/reportes").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Perfil ADMIN (login real) grava previsões")
    void perfilAdmin() throws Exception {
        String token = login("admin", "admin123");
        mockMvc.perform(put("/previsoes").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fonte\":\"X\",\"previsoes\":[{\"local_id\":\"nu-jaime\",\"previsao_hora\":\"2030-01-01T10:00:00-03:00\",\"estimativa\":0.5}]}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Token expira em 30 minutos a partir de agora, qualquer que seja o fuso do servidor")
    void expiracaoTrintaMinutos() throws Exception {
        Instant antes = Instant.now();
        Instant expira = JWT.decode(login("admin", "admin123")).getExpiresAtAsInstant();
        Duration validade = Duration.between(antes, expira);
        assertThat(validade).isBetween(Duration.ofMinutes(29), Duration.ofMinutes(31));
    }

    @Test
    @DisplayName("Token expirado é recusado com 401 token_invalido")
    void tokenExpirado() throws Exception {
        String expirado = JWT.create()
                .withIssuer("RotaSecaSOA")
                .withSubject("admin")
                .withExpiresAt(Instant.now().minusSeconds(60))
                .sign(Algorithm.HMAC256(secret));
        mockMvc.perform(get("/reportes").header("Authorization", "Bearer " + expirado))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro.codigo").value("token_invalido"));
    }

    @Test
    @DisplayName("Token assinado com outro segredo, de outro emissor ou sem assinatura (alg none) é recusado")
    void tokensForjados() throws Exception {
        Instant futuro = Instant.now().plusSeconds(600);
        String outroSegredo = JWT.create().withIssuer("RotaSecaSOA").withSubject("admin").withExpiresAt(futuro)
                .sign(Algorithm.HMAC256("segredo-de-atacante"));
        String outroEmissor = JWT.create().withIssuer("OutraAPI").withSubject("admin").withExpiresAt(futuro)
                .sign(Algorithm.HMAC256(secret));
        String semAssinatura = JWT.create().withIssuer("RotaSecaSOA").withSubject("admin").withExpiresAt(futuro)
                .sign(Algorithm.none());
        String usuarioInexistente = JWT.create().withIssuer("RotaSecaSOA").withSubject("fantasma").withExpiresAt(futuro)
                .sign(Algorithm.HMAC256(secret));

        for (String token : new String[]{outroSegredo, outroEmissor, semAssinatura, usuarioInexistente}) {
            mockMvc.perform(get("/reportes").header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.erro.codigo").value("token_invalido"));
        }
    }

    @Test
    @DisplayName("Login não revela se o usuário existe (mesma resposta para usuário inexistente e senha errada)")
    void loginNaoRevelaUsuario() throws Exception {
        String senhaErrada = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
        String inexistente = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"ninguem\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
        assertThat(inexistente).isEqualTo(senhaErrada);
    }

    @Test
    @DisplayName("Resposta do login não devolve a senha nem o hash")
    void loginNaoVazaSenha() throws Exception {
        String json = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin\",\"senha\":\"admin123\"}"))
                .andReturn().getResponse().getContentAsString();
        assertThat(json).doesNotContain("admin123").doesNotContain("$2a$");
    }

    @Test
    @DisplayName("Content-Type errado no POST /reportes (ex.: ESP32 sem header) é 415, nunca 500")
    void contentTypeErrado() throws Exception {
        mockMvc.perform(post("/reportes").contentType(MediaType.TEXT_PLAIN).content("x"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.erro.codigo").value("requisicao_invalida"));
    }
}
