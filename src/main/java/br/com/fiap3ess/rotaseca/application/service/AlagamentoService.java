package br.com.fiap3ess.rotaseca.application.service;

import br.com.fiap3ess.rotaseca.adapter.in.controller.mapper.AlagamentoMapper;
import br.com.fiap3ess.rotaseca.adapter.in.controller.request.alagamento.DadosRegistroAlagamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.alagamento.DadosAlagamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Alagamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import br.com.fiap3ess.rotaseca.application.port.out.AlagamentoRepository;
import br.com.fiap3ess.rotaseca.application.port.out.CruzamentoRepository;
import br.com.fiap3ess.rotaseca.exception.type.CruzamentoNotFoundException;
import br.com.fiap3ess.rotaseca.shared.util.Bbox;
import br.com.fiap3ess.rotaseca.shared.util.Horario;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.FonteAlagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlagamentoService {
    private final AlagamentoRepository repository;
    private final CruzamentoRepository cruzamentoRepository;
    private final AlagamentoMapper mapper;

    @Transactional
    public DadosAlagamento registrar(DadosRegistroAlagamento dados) {
        Cruzamento cruzamento = cruzamentoRepository.buscarAtivo(dados.localId())
                .orElseThrow(() -> new CruzamentoNotFoundException(dados.localId()));
        Alagamento alagamento = new Alagamento(
                null,
                cruzamento.getId(),
                Horario.parse(dados.registradoEm(), "registrado_em"),
                dados.fonte() == null ? FonteAlagamento.CGE : dados.fonte(),
                Boolean.TRUE.equals(dados.intransitavel()),
                true
        );
        return mapper.toDados(repository.save(alagamento), cruzamento);
    }

    /** bbox = minLon,minLat,maxLon,maxLat (ordem do GeoJSON). Sem "desde", últimas 24 h. */
    @Transactional(readOnly = true)
    public List<DadosAlagamento> listarNaArea(String bbox, String desde) {
        Bbox caixa = Bbox.parse(bbox);
        LocalDateTime inicio = desde == null || desde.isBlank()
                ? Horario.agora().minusDays(1)
                : Horario.parse(desde, "desde");
        return repository.listarAtivosNaArea(caixa.minLat(), caixa.maxLat(), caixa.minLon(), caixa.maxLon(), inicio).stream()
                .map(a -> mapper.toDados(a, cruzamentoRepository.findById(a.getCruzamentoId()).orElseThrow()))
                .toList();
    }
}
