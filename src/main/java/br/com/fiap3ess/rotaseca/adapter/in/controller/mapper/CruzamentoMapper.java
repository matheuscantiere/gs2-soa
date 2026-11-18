package br.com.fiap3ess.rotaseca.adapter.in.controller.mapper;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.cruzamento.DadosCadastroCruzamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento.DadosCruzamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento.DadosDetalhamentoCruzamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento.DadosListagemCruzamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import org.springframework.stereotype.Component;

import static br.com.fiap3ess.rotaseca.adapter.in.controller.mapper.NumeroMapper.numero;

@Component
public class CruzamentoMapper {
    public Cruzamento toDomain(DadosCadastroCruzamento dados) {
        return new Cruzamento(
                dados.id(),
                dados.nome(),
                dados.nomeCurto(),
                dados.via(),
                dados.bairro(),
                dados.latitude(),
                dados.longitude(),
                dados.placeholder(),
                dados.ordem() == null ? 0 : dados.ordem(),
                true
        );
    }

    /** Formato do app mobile: em placeholder a via sai null. */
    public DadosCruzamento toPublico(Cruzamento domain) {
        return new DadosCruzamento(
                domain.getId(),
                domain.getNome(),
                domain.getNomeCurto(),
                domain.isPlaceholder() ? null : domain.getVia(),
                domain.getBairro(),
                numero(domain.getLatitude()),
                numero(domain.getLongitude()),
                domain.isPlaceholder()
        );
    }

    public DadosDetalhamentoCruzamento toDetails(Cruzamento domain) {
        return new DadosDetalhamentoCruzamento(
                domain.getId(),
                domain.getNome(),
                domain.getNomeCurto(),
                domain.isPlaceholder() ? null : domain.getVia(),
                domain.getBairro(),
                numero(domain.getLatitude()),
                numero(domain.getLongitude()),
                domain.isPlaceholder(),
                domain.getOrdem(),
                domain.isAtivo()
        );
    }

    public DadosListagemCruzamento toList(Cruzamento domain) {
        return new DadosListagemCruzamento(
                domain.getId(),
                domain.getNomeCurto(),
                domain.getBairro(),
                domain.isPlaceholder(),
                domain.getOrdem()
        );
    }
}
