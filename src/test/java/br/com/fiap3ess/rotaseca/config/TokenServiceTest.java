package br.com.fiap3ess.rotaseca.config;

import br.com.fiap3ess.rotaseca.config.security.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** No perfil prod a API não sobe com JWT_SECRET fraco; fora dele só avisa. */
class TokenServiceTest {
    private static final String FORTE = "Zq8vN1xR4tW7yB0cE3fH6jK9mP2sU5vX8zA1dG4iL7oQ";

    private static void verificar(String segredo, String... perfis) {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles(perfis);
        ReflectionTestUtils.invokeMethod(new TokenService(segredo, 30, env), "verificarSegredo");
    }

    @Test
    @DisplayName("prod + segredo padrão ou curto: falha ao subir")
    void prodComSegredoFraco() {
        assertThatThrownBy(() -> verificar("12345678", "prod")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> verificar("curto-demais", "prod")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("prod + segredo forte: sobe")
    void prodComSegredoForte() {
        assertThatCode(() -> verificar(FORTE, "prod")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Desenvolvimento (sem perfil prod) + segredo padrão: sobe, só avisa no log")
    void desenvolvimento() {
        assertThatCode(() -> verificar("12345678")).doesNotThrowAnyException();
        assertThatCode(() -> verificar("12345678", "test")).doesNotThrowAnyException();
    }
}
