package br.com.fiap3ess.rotaseca.application.core.specification.contract;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.reporte.DadosReporte;

public interface ValidadorReporte {
    void validar(DadosReporte dados);
}
