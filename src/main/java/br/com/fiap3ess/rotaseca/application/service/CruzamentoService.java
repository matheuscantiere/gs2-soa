package br.com.fiap3ess.rotaseca.application.service;

import br.com.fiap3ess.rotaseca.adapter.in.controller.mapper.CruzamentoMapper;
import br.com.fiap3ess.rotaseca.adapter.in.controller.request.cruzamento.DadosAtualizacaoCruzamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.request.cruzamento.DadosCadastroCruzamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento.DadosCruzamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento.DadosDetalhamentoCruzamento;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.cruzamento.DadosListagemCruzamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import br.com.fiap3ess.rotaseca.application.port.out.CruzamentoRepository;
import br.com.fiap3ess.rotaseca.exception.type.ConflitoException;
import br.com.fiap3ess.rotaseca.exception.type.CruzamentoNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CruzamentoService {
    private final CruzamentoRepository repository;
    private final CruzamentoMapper mapper;

    @Transactional(readOnly = true)
    public List<DadosCruzamento> listarPublico() {
        return repository.listarAtivosOrdenados().stream().map(mapper::toPublico).toList();
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemCruzamento> listarCruzamentos(Pageable paginacao) {
        return repository.findAllByAtivoTrue(paginacao).map(mapper::toList);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoCruzamento detalharCruzamento(String id) {
        return mapper.toDetails(buscarAtivo(id));
    }

    @Transactional
    public DadosDetalhamentoCruzamento cadastrarCruzamento(DadosCadastroCruzamento dados) {
        if (repository.existsById(dados.id())) {
            throw new ConflitoException("Já existe um cruzamento com o id '" + dados.id() + "'!");
        }
        return mapper.toDetails(repository.save(mapper.toDomain(dados)));
    }

    @Transactional
    public DadosDetalhamentoCruzamento atualizarCruzamento(DadosAtualizacaoCruzamento dados) {
        Cruzamento cruzamento = buscarAtivo(dados.id());
        cruzamento.atualizarInformacoes(
                dados.nome(),
                dados.nomeCurto(),
                dados.via(),
                dados.bairro(),
                dados.latitude(),
                dados.longitude(),
                dados.placeholder(),
                dados.ordem()
        );
        return mapper.toDetails(repository.save(cruzamento));
    }

    @Transactional
    public void excluirCruzamento(String id) {
        Cruzamento cruzamento = buscarAtivo(id);
        cruzamento.excluir();
        repository.save(cruzamento);
    }

    private Cruzamento buscarAtivo(String id) {
        return repository.buscarAtivo(id).orElseThrow(() -> new CruzamentoNotFoundException(id));
    }
}
