package br.com.fiap3ess.rotaseca.application.core.specification;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.previsao.DadosPrevisao;
import br.com.fiap3ess.rotaseca.application.core.specification.contract.ValidadorPrevisao;
import br.com.fiap3ess.rotaseca.exception.type.ValidacaoException;
import br.com.fiap3ess.rotaseca.shared.util.Horario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** A previsão do modelo é horária: previsao_hora precisa ser hora cheia (ex.: 18:00:00). */
@Component
public class ValidadorPrevisaoHoraInteira implements ValidadorPrevisao {
    @Override
    public void validar(DadosPrevisao dados) {
        LocalDateTime hora = Horario.parse(dados.previsaoHora(), "previsao_hora");
        if (hora.getMinute() != 0 || hora.getSecond() != 0) {
            throw new ValidacaoException("previsao_hora precisa ser uma hora cheia (minutos e segundos zerados)!");
        }
    }
}
