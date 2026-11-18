package br.com.fiap3ess.rotaseca.application.port.out;

import br.com.fiap3ess.rotaseca.application.core.domain.PrevisaoChuva;

import java.math.BigDecimal;

/** Porta para o serviço externo de chuva (implementada pelo adaptador do Open-Meteo). */
public interface ProvedorChuva {
    PrevisaoChuva consultar(BigDecimal latitude, BigDecimal longitude);
}
