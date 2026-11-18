package br.com.fiap3ess.rotaseca.adapter.out.external;

import br.com.fiap3ess.rotaseca.application.core.domain.PrevisaoChuva;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/** Conversão da resposta do Open-Meteo (JSON de exemplo no formato real da API), sem rede. */
class OpenMeteoServiceTest {
    @Test
    @DisplayName("Converte mm por 15 min em mm/h e lê os horários locais.")
    void converter() {
        String json = """
                {"latitude":-23.68,"longitude":-46.69,
                 "current":{"time":"2026-11-17T18:00","interval":900,"precipitation":3.1},
                 "minutely_15":{"time":["2026-11-17T18:00","2026-11-17T18:15"],"precipitation":[3.1,null]}}
                """;
        PrevisaoChuva chuva = OpenMeteoService.converter(JsonParser.parseString(json).getAsJsonObject(),
                new BigDecimal("-23.68"), new BigDecimal("-46.69"));

        assertThat(chuva.medidoEm()).isEqualTo(LocalDateTime.of(2026, 11, 17, 18, 0));
        assertThat(chuva.intensidadeAtualMmH()).isEqualByComparingTo("12.40");
        assertThat(chuva.prevista()).hasSize(2);
        assertThat(chuva.prevista().get(1).intensidadeMmH()).isEqualByComparingTo("0");
    }
}
