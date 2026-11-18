package br.com.fiap3ess.rotaseca.adapter.in.controller.mapper;

import java.math.BigDecimal;

/** BigDecimal do banco -> número JSON sem zeros à direita (-46.6945910 -> -46.694591). */
final class NumeroMapper {
    private NumeroMapper() {
    }

    static Double numero(BigDecimal valor) {
        return valor == null ? null : valor.doubleValue();
    }
}
