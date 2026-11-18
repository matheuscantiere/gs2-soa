package br.com.fiap3ess.rotaseca.shared.vo.enumeration;

/**
 * Situação da previsão de um cruzamento. No JSON sai em minúsculo
 * ("elevado", "normal", "sem_estimativa"), como o app mobile espera.
 */
public enum StatusRisco {
    ELEVADO,
    NORMAL,
    SEM_ESTIMATIVA;

    public String valor() {
        return name().toLowerCase();
    }
}
