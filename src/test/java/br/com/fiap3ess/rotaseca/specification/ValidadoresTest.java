package br.com.fiap3ess.rotaseca.specification;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.previsao.DadosPrevisao;
import br.com.fiap3ess.rotaseca.adapter.in.controller.request.reporte.DadosReporte;
import br.com.fiap3ess.rotaseca.application.core.specification.ValidadorCoordenadaRegiaoSP;
import br.com.fiap3ess.rotaseca.application.core.specification.ValidadorNivelSensor;
import br.com.fiap3ess.rotaseca.application.core.specification.ValidadorPrevisaoHoraInteira;
import br.com.fiap3ess.rotaseca.exception.type.ParametroInvalidoException;
import br.com.fiap3ess.rotaseca.exception.type.ValidacaoException;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Specifications sem dependência de banco. */
class ValidadoresTest {
    private static DadosReporte reporte(OrigemReporte origem, String lat, String lon, Integer nivel) {
        return new DadosReporte(origem, null, new BigDecimal(lat), new BigDecimal(lon), nivel, null);
    }

    @Test
    @DisplayName("Coordenada: aceita a Vila Géa e recusa o Rio de Janeiro.")
    void coordenada() {
        var validador = new ValidadorCoordenadaRegiaoSP();
        assertThatCode(() -> validador.validar(reporte(OrigemReporte.APP, "-23.68", "-46.69", null)))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> validador.validar(reporte(OrigemReporte.APP, "-22.90", "-43.20", null)))
                .isInstanceOf(ValidacaoException.class);
    }

    @Test
    @DisplayName("Sensor: exige nivel_cm; app não exige.")
    void nivelSensor() {
        var validador = new ValidadorNivelSensor();
        assertThatThrownBy(() -> validador.validar(reporte(OrigemReporte.SENSOR, "-23.68", "-46.69", null)))
                .isInstanceOf(ValidacaoException.class);
        assertThatCode(() -> validador.validar(reporte(OrigemReporte.APP, "-23.68", "-46.69", null)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Previsão: hora cheia passa, 18:30 e texto sem fuso falham.")
    void horaInteira() {
        var validador = new ValidadorPrevisaoHoraInteira();
        assertThatCode(() -> validador.validar(previsao("2030-01-01T18:00:00-03:00"))).doesNotThrowAnyException();
        assertThatThrownBy(() -> validador.validar(previsao("2030-01-01T18:30:00-03:00")))
                .isInstanceOf(ValidacaoException.class);
        assertThatThrownBy(() -> validador.validar(previsao("2030-01-01T18:00:00")))
                .isInstanceOf(ParametroInvalidoException.class);
    }

    private DadosPrevisao previsao(String hora) {
        return new DadosPrevisao("nu-jaime", hora, new BigDecimal("0.1"), null);
    }
}
