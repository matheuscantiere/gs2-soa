package br.com.fiap3ess.rotaseca.application.core.specification;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.previsao.DadosPrevisao;
import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import br.com.fiap3ess.rotaseca.application.core.specification.contract.ValidadorPrevisao;
import br.com.fiap3ess.rotaseca.application.port.out.CruzamentoRepository;
import br.com.fiap3ess.rotaseca.exception.type.CruzamentoNotFoundException;
import br.com.fiap3ess.rotaseca.exception.type.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Só aceita previsão para cruzamento real (ativo e já definido pelo estudo). */
@Component
@RequiredArgsConstructor
public class ValidadorCruzamentoNaoPlaceholder implements ValidadorPrevisao {
    private final CruzamentoRepository repository;

    @Override
    public void validar(DadosPrevisao dados) {
        Cruzamento cruzamento = repository.buscarAtivo(dados.localId())
                .orElseThrow(() -> new CruzamentoNotFoundException(dados.localId()));
        if (cruzamento.isPlaceholder()) {
            throw new ValidacaoException("Cruzamento '" + dados.localId() + "' ainda não foi definido pelo estudo!");
        }
    }
}
