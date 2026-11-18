package br.com.fiap3ess.rotaseca.shared.util;

import br.com.fiap3ess.rotaseca.exception.type.ParametroInvalidoException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Horários do serviço. O banco guarda o horário de São Paulo sem fuso (como a aula guarda data_hora)
 * e a API sempre responde em ISO 8601 com "-03:00" (nunca "Z"), regra do contrato com o app mobile.
 */
public final class Horario {
    public static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    public static final ZoneOffset OFFSET = ZoneOffset.of("-03:00");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private Horario() {
    }

    /** LocalDateTime (horário de SP) -> "2025-01-16T18:00:00-03:00". */
    public static String iso(LocalDateTime horario) {
        return horario == null ? null : horario.atOffset(OFFSET).format(ISO);
    }

    /**
     * Texto ISO 8601 com fuso -> horário de SP. Aceita o "+" de um fuso positivo que chegou
     * como espaço por não ter sido codificado na URL.
     */
    public static LocalDateTime parse(String texto, String parametro) {
        if (texto == null || texto.isBlank()) {
            throw new ParametroInvalidoException(parametro + " é obrigatório.");
        }
        try {
            return OffsetDateTime.parse(texto.trim().replace(' ', '+'))
                    .atZoneSameInstant(ZONA)
                    .toLocalDateTime();
        } catch (DateTimeParseException e) {
            throw new ParametroInvalidoException(parametro + " fora do formato ISO 8601.");
        }
    }

    public static LocalDateTime agora() {
        return LocalDateTime.now(ZONA).truncatedTo(ChronoUnit.SECONDS);
    }

    public static LocalDateTime inicioDaHora(LocalDateTime horario) {
        return horario.truncatedTo(ChronoUnit.HOURS);
    }
}
