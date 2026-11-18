package br.com.fiap3ess.rotaseca.application.core.specification;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.reporte.DadosReporte;
import br.com.fiap3ess.rotaseca.application.core.specification.contract.ValidadorReporte;
import br.com.fiap3ess.rotaseca.exception.type.ValidacaoException;
import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import org.springframework.stereotype.Component;

/** O sensor de nível (ESP32 + HC-SR04) sempre manda a altura da água; a faixa 0–200 cm vem do Bean Validation. */
@Component
public class ValidadorNivelSensor implements ValidadorReporte {
    @Override
    public void validar(DadosReporte dados) {
        if (dados.origem() == OrigemReporte.SENSOR && dados.nivelCm() == null) {
            throw new ValidacaoException("Reporte de sensor precisa informar o nível da água (nivel_cm)!");
        }
    }
}
