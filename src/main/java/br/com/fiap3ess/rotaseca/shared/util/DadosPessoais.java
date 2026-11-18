package br.com.fiap3ess.rotaseca.shared.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Primeira barreira contra dado pessoal no texto livre do reporte: troca e-mail, CPF, telefone e placa
 * por "[removido]" antes de gravar. A detecção completa (nomes, endereços, taint analysis) é do módulo de Cyber.
 */
public final class DadosPessoais {
    public static final String MARCADOR = "[removido]";

    // Ordem importa: CPF (11 dígitos) antes de telefone, que também casaria com ele.
    private static final Map<String, Pattern> REGRAS = new LinkedHashMap<>();

    static {
        REGRAS.put("email", Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+"));
        REGRAS.put("cpf", Pattern.compile("\\b\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}\\b"));
        REGRAS.put("telefone", Pattern.compile("(\\+?55[\\s-]?)?\\(?\\b\\d{2}\\)?[\\s-]?9?\\d{4}[\\s-]?\\d{4}\\b"));
        REGRAS.put("placa", Pattern.compile("\\b[A-Za-z]{3}-?\\d[A-Za-z0-9]\\d{2}\\b"));
    }

    private DadosPessoais() {
    }

    public record Resultado(String texto, boolean removeu) {
    }

    public static Resultado remover(String texto) {
        if (texto == null) {
            return new Resultado(null, false);
        }
        String limpo = texto;
        boolean removeu = false;
        for (Pattern regra : REGRAS.values()) {
            Matcher m = regra.matcher(limpo);
            if (m.find()) {
                removeu = true;
                limpo = m.replaceAll(Matcher.quoteReplacement(MARCADOR));
            }
        }
        return new Resultado(limpo, removeu);
    }
}
