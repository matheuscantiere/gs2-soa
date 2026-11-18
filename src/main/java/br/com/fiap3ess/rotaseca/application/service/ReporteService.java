package br.com.fiap3ess.rotaseca.application.service;

import br.com.fiap3ess.rotaseca.adapter.in.controller.mapper.ReporteMapper;
import br.com.fiap3ess.rotaseca.adapter.in.controller.request.reporte.DadosReporte;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.reporte.DadosDetalhamentoReporte;
import br.com.fiap3ess.rotaseca.adapter.in.controller.response.reporte.DadosListagemReporte;
import br.com.fiap3ess.rotaseca.application.core.specification.contract.ValidadorReporte;
import br.com.fiap3ess.rotaseca.application.port.out.ReporteRepository;
import br.com.fiap3ess.rotaseca.exception.type.RecursoNotFoundException;
import br.com.fiap3ess.rotaseca.application.core.domain.Reporte;
import br.com.fiap3ess.rotaseca.exception.type.ChaveApiInvalidaException;
import br.com.fiap3ess.rotaseca.shared.util.DadosPessoais;
import br.com.fiap3ess.rotaseca.shared.util.Horario;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {
    private final ReporteRepository repository;
    private final ReporteMapper mapper;
    private final List<ValidadorReporte> validadoresReporte;

    @Value("${rotaseca.reportes.chave-api}")
    private String chaveApi;

    @Transactional
    public DadosDetalhamentoReporte receber(DadosReporte dados, String chaveApiInformada) {
        exigirChaveApi(dados, chaveApiInformada);
        //Validações
        validadoresReporte.forEach(validador -> validador.validar(dados));
        DadosPessoais.Resultado descricao = DadosPessoais.remover(dados.descricao());
        Reporte salvo = repository.save(mapper.toDomain(dados, descricao.texto(), Horario.agora()));
        return mapper.toDetails(salvo, descricao.removeu());
    }

    /**
     * Com REPORTES_CHAVE_API definida, sensor e parceiro precisam mandar o header X-Api-Key.
     * O app do cidadão continua sem chave. Sem a variável, ninguém precisa (modo da entrega).
     */
    private void exigirChaveApi(DadosReporte dados, String informada) {
        boolean exige = !chaveApi.isBlank()
                && (dados.origem() == OrigemReporte.SENSOR || dados.origem() == OrigemReporte.PARCEIRO);
        if (exige && (informada == null || !MessageDigest.isEqual(
                chaveApi.getBytes(StandardCharsets.UTF_8), informada.getBytes(StandardCharsets.UTF_8)))) {
            throw new ChaveApiInvalidaException("Reporte de sensor ou parceiro exige o header X-Api-Key válido.");
        }
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemReporte> listar(Pageable paginacao) {
        return repository.findAll(paginacao).map(mapper::toList);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoReporte detalhar(Long id) {
        return repository.findById(id)
                .map(mapper::toDetails)
                .orElseThrow(() -> new RecursoNotFoundException("Reporte " + id + " não existe."));
    }
}
