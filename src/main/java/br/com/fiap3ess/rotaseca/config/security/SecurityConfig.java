package br.com.fiap3ess.rotaseca.config.security;

import br.com.fiap3ess.rotaseca.config.security.filter.LimiteReportesFilter;
import br.com.fiap3ess.rotaseca.config.security.filter.SecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SecurityFilter securityFilter;
    private final LimiteReportesFilter limiteReportesFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOriginPatterns(List.of(
                                    "http://localhost:3000",
                                    "http://127.0.0.1:3000",
                                    "http://localhost:5500",
                                    "http://127.0.0.1:5500",
                                    "http://localhost:8081",   // Expo web (app mobile no navegador)
                                    "http://127.0.0.1:8081",
                                    "http://localhost:19006",
                                    "http://127.0.0.1:19006"
                            ));
                            configuration.setAllowedMethods(List.of(
                                    "GET",
                                    "POST",
                                    "PUT",
                                    "DELETE",
                                    "OPTIONS",
                                    "HEAD"
                            ));
                            configuration.setAllowedHeaders(List.of(
                                    "Authorization",
                                    "Content-Type",
                                    "Accept",
                                    "Origin"
                            ));
                            configuration.setAllowCredentials(true);
                            return configuration;
                        }))
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                                .requestMatchers("/v3/api-docs.yaml",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/health").permitAll()
                                // Página de erro do Spring: sem isso, um erro numa rota pública virava 401
                                .requestMatchers("/error").permitAll()
                                // Administração (precisa de token; o perfil é checado com @PreAuthorize)
                                .requestMatchers(HttpMethod.GET, "/cruzamentos/admin").authenticated()
                                // Endpoints públicos consumidos pelo app mobile (que não envia token)
                                .requestMatchers(HttpMethod.GET,
                                        "/cruzamentos",
                                        "/cruzamentos/*",
                                        "/risco",
                                        "/alertas",
                                        "/alagamentos",
                                        "/chuva").permitAll()
                                // Sensor ESP32, app e parceiros enviam reportes sem login
                                .requestMatchers(HttpMethod.POST, "/reportes").permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) ->
                                RespostaErroJson.escrever(response, HttpStatus.UNAUTHORIZED,
                                        "nao_autenticado", "Informe um token JWT válido (POST /login)."))
                        .accessDeniedHandler((request, response, e) ->
                                RespostaErroJson.escrever(response, HttpStatus.FORBIDDEN,
                                        "acesso_negado", "Seu perfil não tem acesso a este recurso."))
                )
                .addFilterBefore(limiteReportesFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
