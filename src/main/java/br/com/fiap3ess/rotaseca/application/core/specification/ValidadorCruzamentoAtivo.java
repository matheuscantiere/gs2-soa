package br.com.fiap3ess.rotaseca.application.core.specification;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.reporte.DadosReporte;
import br.com.fiap3ess.rotaseca.application.core.specification.contract.ValidadorReporte;
import br.com.fiap3ess.rotaseca.application.port.out.CruzamentoRepository;
import br.com.fiap3ess.rotaseca.exception.type.CruzamentoNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Se o reporte cita um cruzamento, ele precisa existir e estar ativo. */
@Component
@RequiredArgsConstructor
public class ValidadorCruzamentoAtivo implements ValidadorReporte {
    private final CruzamentoRepository repository;

    @Override
    public void validar(DadosReporte dados) {
        if (dados.localId() == null || dados.localId().isBlank()) {
            return;
        }
        if (repository.buscarAtivo(dados.localId()).isEmpty()) {
            throw new CruzamentoNotFoundException(dados.localId());
        }
    }
}
