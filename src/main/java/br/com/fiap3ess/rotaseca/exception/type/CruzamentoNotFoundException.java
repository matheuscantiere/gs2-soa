package br.com.fiap3ess.rotaseca.exception.type;

public class CruzamentoNotFoundException extends RuntimeException {
    public CruzamentoNotFoundException(String id) { super("Cruzamento '" + id + "' não existe."); }
}
