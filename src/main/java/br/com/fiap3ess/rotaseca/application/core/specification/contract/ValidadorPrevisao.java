package br.com.fiap3ess.rotaseca.application.core.specification.contract;

import br.com.fiap3ess.rotaseca.adapter.in.controller.request.previsao.DadosPrevisao;

public interface ValidadorPrevisao {
    void validar(DadosPrevisao dados);
}
