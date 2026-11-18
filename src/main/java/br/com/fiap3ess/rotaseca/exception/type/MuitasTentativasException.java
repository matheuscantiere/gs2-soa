package br.com.fiap3ess.rotaseca.exception.type;

import lombok.Getter;

/** Limite de tentativas/requisições estourado (HTTP 429). */
@Getter
public class MuitasTentativasException extends RuntimeException {
    private final long segundosParaTentarDeNovo;

    public MuitasTentativasException(String mensagem, long segundosParaTentarDeNovo) {
        super(mensagem);
        this.segundosParaTentarDeNovo = segundosParaTentarDeNovo;
    }
}
