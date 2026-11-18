package br.com.fiap3ess.rotaseca.application.core.specification;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.reporte.DadosReporte;
import br.com.fiap3ess.rotaseca.application.core.specification.contract.ValidadorReporte;
import br.com.fiap3ess.rotaseca.exception.type.ValidacaoException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** O serviço cobre a Grande São Paulo (mesma caixa usada pelo app mobile). */
@Component
public class ValidadorCoordenadaRegiaoSP implements ValidadorReporte {
    static final BigDecimal LAT_MIN = new BigDecimal("-24.05");
    static final BigDecimal LAT_MAX = new BigDecimal("-23.35");
    static final BigDecimal LON_MIN = new BigDecimal("-47.10");
    static final BigDecimal LON_MAX = new BigDecimal("-46.30");

    @Override
    public void validar(DadosReporte dados) {
        boolean latOk = dados.latitude().compareTo(LAT_MIN) >= 0 && dados.latitude().compareTo(LAT_MAX) <= 0;
        boolean lonOk = dados.longitude().compareTo(LON_MIN) >= 0 && dados.longitude().compareTo(LON_MAX) <= 0;
        if (!latOk || !lonOk) {
            throw new ValidacaoException("Localização fora da região atendida (Grande São Paulo)!");
        }
    }
}
