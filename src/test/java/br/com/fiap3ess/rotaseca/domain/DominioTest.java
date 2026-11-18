package br.com.fiap3ess.rotaseca.domain;

import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Previsao;
import br.com.fiap3ess.rotaseca.application.core.domain.Reporte;
import br.com.fiap3ess.rotaseca.shared.util.Horario;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.StatusRisco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/** Regras de negócio puras, sem Spring e sem banco. */
class DominioTest {
    private static final BigDecimal LIMIAR = Previsao.LIMIAR_PADRAO;

    @Test
    @DisplayName("Status: sem estimativa, acima do limiar (estrito) e igual ao limiar.")
    void calcularStatus() {
        assertThat(Previsao.calcularStatus(null, LIMIAR)).isEqualTo(StatusRisco.SEM_ESTIMATIVA);
        assertThat(Previsao.calcularStatus(new BigDecimal("0.01084"), LIMIAR)).isEqualTo(StatusRisco.ELEVADO);
        assertThat(Previsao.calcularStatus(new BigDecimal("0.01083"), LIMIAR)).isEqualTo(StatusRisco.NORMAL);
        assertThat(Previsao.calcularStatus(new BigDecimal("0.010830"), null)).isEqualTo(StatusRisco.NORMAL);
    }

    @Test
    @DisplayName("Previsão vale por uma hora a partir da hora cheia.")
    void validade() {
        Previsao p = Previsao.semEstimativa("nu-jaime", LocalDateTime.of(2025, 1, 16, 18, 0));
        assertThat(p.validaAte()).isEqualTo(LocalDateTime.of(2025, 1, 16, 19, 0));
        assertThat(p.vigenteEm(LocalDateTime.of(2025, 1, 16, 18, 59))).isTrue();
        assertThat(p.vigenteEm(LocalDateTime.of(2025, 1, 16, 19, 0))).isFalse();
    }

    @Test
    @DisplayName("Atualizar estimativa recalcula o status.")
    void atualizarEstimativa() {
        Previsao p = Previsao.doModelo("nu-jaime", LocalDateTime.of(2030, 1, 1, 10, 0), new BigDecimal("0.5"), null, "ML");
        assertThat(p.getStatus()).isEqualTo(StatusRisco.ELEVADO);
        p.atualizarEstimativa(new BigDecimal("0.001"), null, "ML");
        assertThat(p.getStatus()).isEqualTo(StatusRisco.NORMAL);
    }

    @Test
    @DisplayName("Faixa do nível da água igual aos LEDs do sensor.")
    void faixaReporte() {
        assertThat(reporte(null).faixa()).isNull();
        assertThat(reporte(4).faixa()).isEqualTo("seco");
        assertThat(reporte(5).faixa()).isEqualTo("lamina");
        assertThat(reporte(14).faixa()).isEqualTo("lamina");
        assertThat(reporte(15).faixa()).isEqualTo("intransitavel_a_pe");
    }

    @Test
    @DisplayName("Cruzamento: atualizar ignora nulos/brancos e excluir é lógico.")
    void cruzamento() {
        Cruzamento c = new Cruzamento("x", "Nome", "Curto", "Via", null, null, null, true, 1, true);
        c.atualizarInformacoes(" ", null, null, "Bairro", null, null, false, null);
        assertThat(c.getNome()).isEqualTo("Nome");
        assertThat(c.getBairro()).isEqualTo("Bairro");
        assertThat(c.isPlaceholder()).isFalse();
        c.excluir();
        assertThat(c.isAtivo()).isFalse();
    }

    @Test
    @DisplayName("Horário sai sempre com -03:00 e entrada em UTC é convertida para São Paulo.")
    void horario() {
        assertThat(Horario.iso(LocalDateTime.of(2025, 1, 16, 18, 0))).isEqualTo("2025-01-16T18:00:00-03:00");
        assertThat(Horario.parse("2025-01-16T21:48:00Z", "em")).isEqualTo(LocalDateTime.of(2025, 1, 16, 18, 48));
        assertThat(Horario.parse("2025-01-16T18:48:00 03:00", "em")) // "+" não codificado vira espaço
                .isEqualTo(LocalDateTime.of(2025, 1, 16, 12, 48));
    }

    private Reporte reporte(Integer nivel) {
        return new Reporte(null, OrigemReporte.SENSOR, null, BigDecimal.ONE, BigDecimal.ONE, nivel, null, null);
    }
}
