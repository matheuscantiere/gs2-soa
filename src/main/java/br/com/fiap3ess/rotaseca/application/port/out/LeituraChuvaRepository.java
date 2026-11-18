package br.com.fiap3ess.rotaseca.application.port.out;

import br.com.fiap3ess.rotaseca.application.core.domain.LeituraChuva;

public interface LeituraChuvaRepository {
    LeituraChuva save(LeituraChuva leitura);
}
