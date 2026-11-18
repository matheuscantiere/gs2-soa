package br.com.fiap3ess.rotaseca.application.service;

import br.com.fiap3ess.rotaseca.adapter.in.controller.mapper.RiscoMapper;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.risco.DadosLoteRisco;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.risco.DadosRisco;
import br.com.fiap3ess.rotaseca.application.core.domain.Alagamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Previsao;
import br.com.fiap3ess.rotaseca.application.port.out.AlagamentoRepository;
import br.com.fiap3ess.rotaseca.application.port.out.CruzamentoRepository;
import br.com.fiap3ess.rotaseca.application.port.out.PrevisaoRepository;
import br.com.fiap3ess.rotaseca.exception.type.CruzamentoNotFoundException;
import br.com.fiap3ess.rotaseca.exception.type.ParametroInvalidoException;
import br.com.fiap3ess.rotaseca.shared.util.Bbox;
import br.com.fiap3ess.rotaseca.shared.util.Horario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Monta a previsão de risco de um cruzamento como o app mobile espera.
 * Com "em", responde como teria respondido naquele instante (usado no replay de 16/01/2025).
 */
@Service
@RequiredArgsConstructor
public class RiscoService {
    private final CruzamentoRepository cruzamentoRepository;
    private final PrevisaoRepository previsaoRepository;
    private final AlagamentoRepository alagamentoRepository;
    private final RiscoMapper mapper;

    @Transactional(readOnly = true)
    public DadosRisco consultar(String localId, String em) {
        Cruzamento cruzamento = cruzamentoRepository.buscarAtivo(localId)
                .orElseThrow(() -> new CruzamentoNotFoundException(localId));
        return calcular(cruzamento, instante(em));
    }

    @Transactional(readOnly = true)
    public DadosLoteRisco consultarLote(String locais, String em) {
        Set<String> ids = new LinkedHashSet<>();
        for (String id : locais.split(",")) {
            if (!id.isBlank()) {
                ids.add(id.trim());
            }
        }
        if (ids.isEmpty()) {
            throw new ParametroInvalidoException("Informe ao menos um local.");
        }
        LocalDateTime instante = instante(em);
        List<DadosRisco> riscos = new ArrayList<>();
        List<String> naoEncontrados = new ArrayList<>();
        for (String id : ids) {
            cruzamentoRepository.buscarAtivo(id).ifPresentOrElse(
                    c -> riscos.add(calcular(c, instante)),
                    () -> naoEncontrados.add(id));
        }
        return new DadosLoteRisco(riscos, naoEncontrados);
    }

    /** Risco de todos os cruzamentos (com coordenada) dentro da área — a camada aberta do mapa de risco. */
    @Transactional(readOnly = true)
    public List<DadosRisco> consultarArea(String bbox, String em) {
        Bbox caixa = Bbox.parse(bbox);
        LocalDateTime instante = instante(em);
        return cruzamentoRepository.listarAtivosComCoordenada().stream()
                .filter(c -> caixa.contem(c.getLatitude(), c.getLongitude()))
                .map(c -> calcular(c, instante))
                .toList();
    }

    /**
     * Histórico de alertas do cruzamento desde "desde": previsões "elevado" e confirmações do CGE,
     * mais recentes primeiro, no mesmo formato de /risco.
     */
    @Transactional(readOnly = true)
    public List<DadosRisco> listarAlertas(String localId, String desde) {
        Cruzamento cruzamento = cruzamentoRepository.buscarAtivo(localId)
                .orElseThrow(() -> new CruzamentoNotFoundException(localId));
        LocalDateTime inicio = desde == null || desde.isBlank()
                ? Horario.agora().minusDays(1)
                : Horario.parse(desde, "desde");

        Map<LocalDateTime, DadosRisco> porHora = new TreeMap<>(Comparator.reverseOrder());
        for (Previsao previsao : previsaoRepository.listarElevadasDesde(localId, inicio)) {
            Alagamento confirmacao = alagamentoRepository.primeiraConfirmacaoCge(
                    localId, previsao.getPrevisaoHora(), previsao.validaAte().minusSeconds(1)).orElse(null);
            porHora.put(previsao.getPrevisaoHora(), mapper.toRisco(cruzamento, previsao, confirmacao));
        }
        for (Alagamento alagamento : alagamentoRepository.listarConfirmacoesCgeDesde(localId, inicio)) {
            Previsao previsao = previsaoRepository.buscarVigente(localId, alagamento.getRegistradoEm())
                    .orElseGet(() -> Previsao.semEstimativa(localId, Horario.inicioDaHora(alagamento.getRegistradoEm())));
            porHora.putIfAbsent(previsao.getPrevisaoHora(), mapper.toRisco(cruzamento, previsao, alagamento));
        }
        return List.copyOf(porHora.values());
    }

    private DadosRisco calcular(Cruzamento cruzamento, LocalDateTime instante) {
        Previsao previsao = previsaoRepository.buscarVigente(cruzamento.getId(), instante)
                .orElseGet(() -> Previsao.semEstimativa(cruzamento.getId(), Horario.inicioDaHora(instante)));
        Alagamento confirmacao = alagamentoRepository
                .primeiraConfirmacaoCge(cruzamento.getId(), previsao.getPrevisaoHora(), instante)
                .orElse(null);
        return mapper.toRisco(cruzamento, previsao, confirmacao);
    }

    private LocalDateTime instante(String em) {
        return em == null || em.isBlank() ? Horario.agora() : Horario.parse(em, "em");
    }
}
