package br.com.fiap3ess.rotaseca.adapter.in.controller.mapper;

import br.com.fiap3ess.rotaseca.adapter.in.controller.response.risco.DadosRisco;
import br.com.fiap3ess.rotaseca.application.core.domain.Alagamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Cruzamento;
import br.com.fiap3ess.rotaseca.application.core.domain.Previsao;
import br.com.fiap3ess.rotaseca.shared.util.Horario;
import org.springframework.stereotype.Component;

import static br.com.fiap3ess.rotaseca.adapter.in.controller.mapper.NumeroMapper.numero;

@Component
public class RiscoMapper {
    /** confirmacao = primeiro alagamento do CGE dentro da janela da previsão, ou null. */
    public DadosRisco toRisco(Cruzamento cruzamento, Previsao previsao, Alagamento confirmacao) {
        boolean confirmado = confirmacao != null && confirmacao.confirmadoPeloCge();
        return new DadosRisco(
                cruzamento.getId(),
                cruzamento.getNome(),
                Horario.iso(previsao.getPrevisaoHora()),
                Horario.iso(previsao.validaAte()),
                numero(previsao.getEstimativa()),
                numero(previsao.getLimiar()),
                previsao.getStatus().valor(),
                confirmado,
                confirmado ? Horario.iso(confirmacao.getRegistradoEm()) : null
        );
    }
}
