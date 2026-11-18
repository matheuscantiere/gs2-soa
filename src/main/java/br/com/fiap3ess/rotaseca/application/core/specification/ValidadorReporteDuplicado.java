package br.com.fiap3ess.rotaseca.application.core.specification;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.reporte.DadosReporte;
import br.com.fiap3ess.rotaseca.application.core.specification.contract.ValidadorReporte;
import br.com.fiap3ess.rotaseca.application.port.out.ReporteRepository;
import br.com.fiap3ess.rotaseca.exception.type.ReporteDuplicadoException;
import br.com.fiap3ess.rotaseca.shared.util.Horario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evita enxurrada: a mesma origem só reporta o mesmo cruzamento (ou, sem cruzamento, o mesmo ponto
 * num raio de ~110 m) uma vez a cada 5 minutos.
 */
@Component
@RequiredArgsConstructor
public class ValidadorReporteDuplicado implements ValidadorReporte {
    static final int JANELA_MINUTOS = 5;
    /** 0,001 grau ≈ 110 m na latitude de São Paulo. */
    static final BigDecimal RAIO_GRAUS = new BigDecimal("0.001");

    private final ReporteRepository repository;

    @Override
    public void validar(DadosReporte dados) {
        LocalDateTime desde = Horario.agora().minusMinutes(JANELA_MINUTOS);
        boolean duplicado = dados.localId() == null || dados.localId().isBlank()
                ? repository.existeRecentePerto(dados.origem(),
                        dados.latitude().subtract(RAIO_GRAUS), dados.latitude().add(RAIO_GRAUS),
                        dados.longitude().subtract(RAIO_GRAUS), dados.longitude().add(RAIO_GRAUS), desde)
                : repository.existeRecente(dados.origem(), dados.localId(), desde);
        if (duplicado) {
            throw new ReporteDuplicadoException(
                    "Já existe reporte desta origem para este local nos últimos " + JANELA_MINUTOS + " minutos!");
        }
    }
}
