package br.com.fiap3ess.rotaseca.application.service;

import br.com.fiap3ess.rotaseca.adapter.in.controller.mapper.ChuvaMapper;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.chuva.DadosChuva;
import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import br.com.fiap3ess.rotaseca.application.core.domain.LeituraChuva;
import br.com.fiap3ess.rotaseca.application.core.domain.PrevisaoChuva;
import br.com.fiap3ess.rotaseca.application.port.out.CruzamentoRepository;
import br.com.fiap3ess.rotaseca.application.port.out.LeituraChuvaRepository;
import br.com.fiap3ess.rotaseca.application.port.out.ProvedorChuva;
import br.com.fiap3ess.rotaseca.exception.type.ParametroInvalidoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChuvaService {
    private final ProvedorChuva provedorChuva;
    private final CruzamentoRepository cruzamentoRepository;
    private final LeituraChuvaRepository leituraChuvaRepository;
    private final ChuvaMapper mapper;

    public DadosChuva consultar(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            throw new ParametroInvalidoException("Informe lat e lon.");
        }
        if (latitude.abs().compareTo(BigDecimal.valueOf(90)) > 0
                || longitude.abs().compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new ParametroInvalidoException("lat/lon fora da faixa válida.");
        }
        return mapper.toDados(provedorChuva.consultar(latitude, longitude));
    }

    /** Job de 15 em 15 min: grava a chuva atual e a prevista de cada cruzamento com coordenada. */
    @Transactional
    public int coletarParaCruzamentos() {
        int gravadas = 0;
        for (Cruzamento cruzamento : cruzamentoRepository.listarAtivosComCoordenada()) {
            try {
                PrevisaoChuva chuva = provedorChuva.consultar(cruzamento.getLatitude(), cruzamento.getLongitude());
                leituraChuvaRepository.save(new LeituraChuva(null, cruzamento.getId(), chuva.medidoEm(),
                        chuva.intensidadeAtualMmH(), false, chuva.fonte()));
                gravadas++;
                for (PrevisaoChuva.Ponto ponto : chuva.prevista()) {
                    if (ponto.hora().isAfter(chuva.medidoEm())) {
                        leituraChuvaRepository.save(new LeituraChuva(null, cruzamento.getId(), ponto.hora(),
                                ponto.intensidadeMmH(), true, chuva.fonte()));
                        gravadas++;
                    }
                }
            } catch (RuntimeException e) {
                log.warn("Falha ao coletar chuva do cruzamento {}: {}", cruzamento.getId(), e.getMessage());
            }
        }
        return gravadas;
    }
}
