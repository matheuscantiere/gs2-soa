package br.com.fiap3ess.rotaseca.application.core.domain;

import br.com.fiap3ess.rotaseca.shared.vo.enumeration.StatusRisco;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Probabilidade de o CGE registrar alagamento no cruzamento na hora seguinte
 * (equivale à "ProbabilidadeTrecho" da proposta). Vem do modelo de ML.
 *
 * O status é gravado: quando o ML manda a estimativa, ele é calculado pelo limiar;
 * no caso real de 16/01/2025 a estimativa não foi divulgada (null), mas o aviso foi "elevado".
 */
@Getter
@AllArgsConstructor
public class Previsao {
    public static final BigDecimal LIMIAR_PADRAO = new BigDecimal("0.01083");

    private Long id;
    private String cruzamentoId;
    private LocalDateTime previsaoHora;
    private BigDecimal estimativa;
    private BigDecimal limiar;
    private StatusRisco status;
    private String fonte;

    /** null -> sem estimativa; acima do limiar (estrito) -> elevado; igual ou abaixo -> normal. */
    public static StatusRisco calcularStatus(BigDecimal estimativa, BigDecimal limiar) {
        if (estimativa == null) {
            return StatusRisco.SEM_ESTIMATIVA;
        }
        BigDecimal limite = limiar == null ? LIMIAR_PADRAO : limiar;
        return estimativa.compareTo(limite) > 0 ? StatusRisco.ELEVADO : StatusRisco.NORMAL;
    }

    /** Previsão nova vinda do modelo de ML. */
    public static Previsao doModelo(
            String cruzamentoId,
            LocalDateTime previsaoHora,
            BigDecimal estimativa,
            BigDecimal limiar,
            String fonte) {
        BigDecimal limite = limiar == null ? LIMIAR_PADRAO : limiar;
        return new Previsao(null, cruzamentoId, previsaoHora, estimativa, limite,
                calcularStatus(estimativa, limite), fonte);
    }

    /** Resposta quando não existe previsão para a hora pedida (fora do estudo ou sem chuva medida). */
    public static Previsao semEstimativa(String cruzamentoId, LocalDateTime previsaoHora) {
        return new Previsao(null, cruzamentoId, previsaoHora, null, LIMIAR_PADRAO,
                StatusRisco.SEM_ESTIMATIVA, null);
    }

    /** A previsão é horária: vale por 1 hora a partir de previsaoHora. */
    public LocalDateTime validaAte() {
        return previsaoHora.plusHours(1);
    }

    public boolean vigenteEm(LocalDateTime instante) {
        return !instante.isBefore(previsaoHora) && instante.isBefore(validaAte());
    }

    /** O modelo reenviou a mesma hora: atualiza a estimativa e recalcula o status. */
    public void atualizarEstimativa(BigDecimal estimativa, BigDecimal limiar, String fonte) {
        this.estimativa = estimativa;
        this.limiar = limiar == null ? LIMIAR_PADRAO : limiar;
        this.status = calcularStatus(estimativa, this.limiar);
        this.fonte = fonte;
    }
}
