package br.com.fiap3ess.rotaseca.controller;

import br.com.fiap3ess.rotaseca.application.core.domain.PrevisaoChuva;
import br.com.fiap3ess.rotaseca.application.port.out.ProvedorChuva;
import br.com.fiap3ess.rotaseca.exception.type.ServicoExternoException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

/** O Open-Meteo é substituído por um mock: o teste não depende de internet. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChuvaControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProvedorChuva provedorChuva;

    @Test
    @DisplayName("Expectativa: devolve a chuva atual e prevista em mm/h com horários -03:00.")
    void chuvaCenario1() throws Exception {
        when(provedorChuva.consultar(any(), any())).thenReturn(new PrevisaoChuva(
                new BigDecimal("-23.68"), new BigDecimal("-46.69"),
                LocalDateTime.of(2026, 11, 17, 18, 0), new BigDecimal("12.40"),
                List.of(new PrevisaoChuva.Ponto(LocalDateTime.of(2026, 11, 17, 18, 15), new BigDecimal("20.00"))),
                "Open-Meteo"));

        mockMvc.perform(get("/chuva").param("lat", "-23.68").param("lon", "-46.69"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intensidade_atual_mm_h").value(12.4))
                .andExpect(jsonPath("$.medido_em").value("2026-11-17T18:00:00-03:00"))
                .andExpect(jsonPath("$.prevista[0].intensidade_mm_h").value(20.0));
    }

    @Test
    @DisplayName("Expectativa: Open-Meteo fora do ar retorna 502.")
    void chuvaCenario2() throws Exception {
        when(provedorChuva.consultar(any(), any())).thenThrow(new ServicoExternoException("fora", null));

        mockMvc.perform(get("/chuva").param("lat", "-23.68").param("lon", "-46.69"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.erro.codigo").value("servico_externo_indisponivel"));
    }

    @Test
    @DisplayName("Expectativa: lat inválida retorna 400.")
    void chuvaCenario3() throws Exception {
        mockMvc.perform(get("/chuva").param("lat", "abc").param("lon", "-46.69"))
                .andExpect(status().isBadRequest());
    }
}
