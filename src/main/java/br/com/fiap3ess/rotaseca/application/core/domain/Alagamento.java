package br.com.fiap3ess.rotaseca.application.core.domain;

import br.com.fiap3ess.rotaseca.shared.vo.enumeration.FonteAlagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/** Ponto de alagamento registrado por órgão oficial (equivale ao "PontoAlagamento" da proposta). */
@Getter
@AllArgsConstructor
public class Alagamento {
    private Long id;
    private String cruzamentoId;
    private LocalDateTime registradoEm;
    private FonteAlagamento fonte;
    private boolean intransitavel;
    private boolean ativo;

    /** A rota só é bloqueada quando o CGE confirma; a previsão sozinha nunca bloqueia. */
    public boolean confirmadoPeloCge() {
        return ativo && fonte == FonteAlagamento.CGE;
    }
}
