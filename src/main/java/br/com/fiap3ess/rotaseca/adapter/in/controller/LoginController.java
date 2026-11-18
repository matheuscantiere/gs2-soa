package br.com.fiap3ess.rotaseca.adapter.in.controller;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.usuario.DadosLogin;
import br.com.fiap3ess.rotaseca.application.core.domain.Usuario;
import br.com.fiap3ess.rotaseca.config.security.LimitadorTentativas;
import br.com.fiap3ess.rotaseca.config.security.service.TokenService;
import br.com.fiap3ess.rotaseca.config.security.service.dto.DadosTokenJWT;
import br.com.fiap3ess.rotaseca.exception.type.MuitasTentativasException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/login")
@Tag(name = "Autenticação")
public class LoginController {
    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final LimitadorTentativas limitador;
    private final int maxTentativas;
    private final Duration bloqueio;

    public LoginController(
            AuthenticationManager manager,
            TokenService tokenService,
            LimitadorTentativas limitador,
            @Value("${rotaseca.limite.login-tentativas}") int maxTentativas,
            @Value("${rotaseca.limite.login-bloqueio-minutos}") long bloqueioMinutos) {
        this.manager = manager;
        this.tokenService = tokenService;
        this.limitador = limitador;
        this.maxTentativas = maxTentativas;
        this.bloqueio = Duration.ofMinutes(bloqueioMinutos);
    }

    @PostMapping
    @Operation(summary = "Gera o token JWT. Após 5 senhas erradas, o login fica bloqueado por 15 min (HTTP 429)")
    public ResponseEntity<DadosTokenJWT> efetuarLogin(@RequestBody @Valid DadosLogin dados, HttpServletRequest request) {
        // Bloqueio por IP + login: um atacante não consegue testar senhas em sequência
        String chave = "login:" + request.getRemoteAddr() + ":" + dados.login().trim().toLowerCase();
        if (limitador.contagem(chave) >= maxTentativas) {
            throw new MuitasTentativasException(
                    "Muitas tentativas de login. Tente novamente mais tarde.", limitador.segundosRestantes(chave));
        }
        try {
            var token = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
            Authentication authentication = manager.authenticate(token);
            limitador.limpar(chave);
            String tokenJWT = tokenService.generateToken((Usuario) authentication.getPrincipal());
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
        } catch (AuthenticationException e) {
            limitador.registrar(chave, bloqueio);
            throw e;
        }
    }
}
