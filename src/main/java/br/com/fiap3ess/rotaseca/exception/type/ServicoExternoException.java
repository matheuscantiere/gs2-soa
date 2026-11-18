package br.com.fiap3ess.rotaseca.exception.type;

public class ServicoExternoException extends RuntimeException {
    public ServicoExternoException(String mensagem, Throwable causa) { super(mensagem, causa); }
}
