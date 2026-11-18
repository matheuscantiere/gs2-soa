package br.com.fiap3ess.rotaseca.config.security.service;

import br.com.fiap3ess.rotaseca.application.core.domain.Usuario;
import com.auth0.jwt.JWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class TokenService {
    private static final String ISSUER = "RotaSecaSOA";
    private static final String SEGREDO_DESENVOLVIMENTO = "12345678";
    private static final int TAMANHO_MINIMO_SEGREDO = 32;

    private final String secret;
    private final long expiracaoMinutos;
    private final Environment environment;

    public TokenService(
            @Value("${api.security.token.secret}") String secret,
            @Value("${api.security.token.expiracao-minutos}") long expiracaoMinutos,
            Environment environment) {
        this.secret = secret;
        this.expiracaoMinutos = expiracaoMinutos;
        this.environment = environment;
    }

    /**
     * Com segredo fraco qualquer um forja tokens. No perfil "prod" a API se recusa a subir;
     * fora dele (desenvolvimento, correção local) só avisa no log.
     */
    @PostConstruct
    void verificarSegredo() {
        boolean fraco = secret == null || secret.length() < TAMANHO_MINIMO_SEGREDO
                || SEGREDO_DESENVOLVIMENTO.equals(secret);
        if (!fraco) {
            return;
        }
        if (environment.matchesProfiles("prod")) {
            throw new IllegalStateException("JWT_SECRET fraco ou padrão no perfil prod: defina JWT_SECRET com "
                    + TAMANHO_MINIMO_SEGREDO + "+ caracteres aleatórios (ex.: openssl rand -base64 48).");
        }
        log.warn("JWT_SECRET fraco ou padrão de desenvolvimento! Em produção use SPRING_PROFILES_ACTIVE=prod "
                + "e JWT_SECRET com {}+ caracteres aleatórios.", TAMANHO_MINIMO_SEGREDO);
    }

    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getLogin())
                    .withClaim("perfil", usuario.getPerfil().name())
                    .withExpiresAt(dataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
            throw new RuntimeException("Erro ao gerar o Token JWT!", ex);
        }
    }

    /** Token inválido ou expirado vira BadCredentialsException (401), nunca erro 500. */
    public String getSubject(String tokenJWT) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException ex) {
            throw new BadCredentialsException("Token JWT inválido ou expirado!", ex);
        }
    }

    /**
     * Instant.now() é absoluto (UTC). A versão da aula usava LocalDateTime.now() + offset -03:00, que
     * depende do fuso do servidor: numa VM em UTC o token valia 3h30 em vez de 30 min.
     */
    private Instant dataExpiracao() {
        return Instant.now().plus(expiracaoMinutos, ChronoUnit.MINUTES);
    }
}
