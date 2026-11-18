package br.com.fiap3ess.rotaseca.application.service;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.previsao.DadosLotePrevisao;
import br.com.fiap3ess.rotaseca.adapter.in.controller.request.previsao.DadosPrevisao;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.risco.DadosResultadoPrevisoes;
import br.com.fiap3ess.rotaseca.application.core.domain.Previsao;
import br.com.fiap3ess.rotaseca.application.core.specification.contract.ValidadorPrevisao;
import br.com.fiap3ess.rotaseca.application.port.out.PrevisaoRepository;
import br.com.fiap3ess.rotaseca.shared.util.Horario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Recebe as probabilidades calculadas pelo modelo de ML (upsert por cruzamento + hora). */
@Service
@RequiredArgsConstructor
public class PrevisaoService {
    private final PrevisaoRepository repository;
    private final List<ValidadorPrevisao> validadoresPrevisao;

    @Transactional
    public DadosResultadoPrevisoes gravarLote(DadosLotePrevisao dados) {
        //Validações (o lote inteiro falha se um item for inválido)
        dados.previsoes().forEach(item -> validadoresPrevisao.forEach(validador -> validador.validar(item)));

        for (DadosPrevisao item : dados.previsoes()) {
            LocalDateTime hora = Horario.parse(item.previsaoHora(), "previsao_hora");
            Previsao previsao = repository.buscarPorHora(item.localId(), hora)
                    .map(existente -> {
                        existente.atualizarEstimativa(item.estimativa(), item.limiar(), dados.fonte());
                        return existente;
                    })
                    .orElseGet(() -> Previsao.doModelo(item.localId(), hora, item.estimativa(), item.limiar(), dados.fonte()));
            repository.save(previsao);
        }
        return new DadosResultadoPrevisoes(dados.previsoes().size());
    }
}
