package br.com.fiap3ess.rotaseca.repository;

import br.com.fiap3ess.rotaseca.adapter.out.repository.AlagamentoRepositoryImpl;
import br.com.fiap3ess.rotaseca.adapter.out.repository.PrevisaoRepositoryImpl;
import br.com.fiap3ess.rotaseca.adapter.out.repository.mapper.AlagamentoEntityMapper;
import br.com.fiap3ess.rotaseca.adapter.out.repository.mapper.PrevisaoEntityMapper;
import br.com.fiap3ess.rotaseca.application.core.domain.Alagamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Previsao;
import br.com.fiap3ess.rotaseca.application.port.out.AlagamentoRepository;
import br.com.fiap3ess.rotaseca.application.port.out.PrevisaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/** Consultas JPQL da janela da previsão e da confirmação do CGE, sobre o replay gravado pela V9. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({PrevisaoRepositoryImpl.class, PrevisaoEntityMapper.class,
        AlagamentoRepositoryImpl.class, AlagamentoEntityMapper.class})
class PrevisaoAlagamentoRepositoryTest {
    private static final LocalDateTime AS_18_00 = LocalDateTime.of(2025, 1, 16, 18, 0);

    @Autowired
    PrevisaoRepository previsaoRepository;

    @Autowired
    AlagamentoRepository alagamentoRepository;

    @Test
    @DisplayName("Expectativa: previsão das 18:00 vigente até 18:59:59 e não às 19:00.")
    void buscarVigenteCenario1() {
        assertThat(previsaoRepository.buscarVigente("nu-jaime", AS_18_00.plusMinutes(59).plusSeconds(59))).isPresent();
        assertThat(previsaoRepository.buscarVigente("nu-jaime", AS_18_00.plusHours(1))).isEmpty();
        assertThat(previsaoRepository.buscarVigente("nu-jaime", AS_18_00.minusSeconds(1))).isEmpty();
    }

    @Test
    @DisplayName("Expectativa: com duas previsões, vale a da hora corrente.")
    void buscarVigenteCenario2() {
        LocalDateTime hora = LocalDateTime.of(2030, 5, 6, 10, 0);
        previsaoRepository.save(Previsao.doModelo("nu-jaime", hora, new BigDecimal("0.5"), null, "TESTE"));
        previsaoRepository.save(Previsao.doModelo("nu-jaime", hora.plusHours(1), new BigDecimal("0.001"), null, "TESTE"));

        Optional<Previsao> vigente = previsaoRepository.buscarVigente("nu-jaime", hora.plusMinutes(70));
        assertThat(vigente).map(Previsao::getPrevisaoHora).contains(hora.plusHours(1));
    }

    @Test
    @DisplayName("Expectativa: confirmação do CGE (18:48) só aparece em janelas que a incluem.")
    void primeiraConfirmacaoCgeCenario1() {
        assertThat(alagamentoRepository.primeiraConfirmacaoCge("nu-jaime", AS_18_00, AS_18_00.plusMinutes(50)))
                .map(Alagamento::getRegistradoEm)
                .contains(AS_18_00.plusMinutes(48));
        assertThat(alagamentoRepository.primeiraConfirmacaoCge("nu-jaime", AS_18_00, AS_18_00.plusMinutes(47)))
                .isEmpty();
    }

    @Test
    @DisplayName("Expectativa: bbox encontra o alagamento do nu-jaime e uma área longe não encontra.")
    void listarAtivosNaAreaCenario1() {
        LocalDateTime desde = LocalDateTime.of(2025, 1, 1, 0, 0);
        assertThat(alagamentoRepository.listarAtivosNaArea(new BigDecimal("-23.75"), new BigDecimal("-23.60"),
                new BigDecimal("-46.75"), new BigDecimal("-46.60"), desde)).hasSize(1);
        assertThat(alagamentoRepository.listarAtivosNaArea(new BigDecimal("-23.50"), new BigDecimal("-23.40"),
                new BigDecimal("-46.75"), new BigDecimal("-46.60"), desde)).isEmpty();
    }
}
