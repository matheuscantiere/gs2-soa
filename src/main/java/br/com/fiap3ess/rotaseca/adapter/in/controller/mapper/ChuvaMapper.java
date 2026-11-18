package br.com.fiap3ess.rotaseca.adapter.in.controller.mapper;

import br.com.fiap3ess.rotaseca.adapter.in.controller.response.chuva.DadosChuva;
import br.com.fiap3ess.rotaseca.application.core.domain.PrevisaoChuva;
import br.com.fiap3ess.rotaseca.shared.util.Horario;
import org.springframework.stereotype.Component;

import static br.com.fiap3ess.rotaseca.adapter.in.controller.mapper.NumeroMapper.numero;

@Component
public class ChuvaMapper {
    public DadosChuva toDados(PrevisaoChuva chuva) {
        return new DadosChuva(
                numero(chuva.latitude()),
                numero(chuva.longitude()),
                Horario.iso(chuva.medidoEm()),
                numero(chuva.intensidadeAtualMmH()),
                chuva.prevista().stream()
                        .map(p -> new DadosChuva.DadosPontoChuva(Horario.iso(p.hora()), numero(p.intensidadeMmH())))
                        .toList(),
                chuva.fonte()
        );
    }
}
