package br.com.fiap3ess.rotaseca.config.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contador por chave em janela fixa (ex.: "login:IP:usuario", "reporte:IP").
 * ponytail: guarda em memória de uma instância só; com várias instâncias da API, trocar por Redis/Bucket4j.
 */
@Component
public class LimitadorTentativas {
    private record Janela(Instant expiraEm, int contagem) {
    }

    private final ConcurrentHashMap<String, Janela> janelas = new ConcurrentHashMap<>();

    /** Soma 1 na chave e devolve a contagem dentro da janela atual. */
    public int registrar(String chave, Duration duracao) {
        Instant agora = Instant.now();
        return janelas.compute(chave, (k, j) -> j == null || !agora.isBefore(j.expiraEm())
                ? new Janela(agora.plus(duracao), 1)
                : new Janela(j.expiraEm(), j.contagem() + 1)).contagem();
    }

    public int contagem(String chave) {
        Janela j = janelas.get(chave);
        return j == null || !Instant.now().isBefore(j.expiraEm()) ? 0 : j.contagem();
    }

    /** Segundos até a janela da chave acabar (mínimo 1). */
    public long segundosRestantes(String chave) {
        Janela j = janelas.get(chave);
        return j == null ? 0 : Math.max(1, Duration.between(Instant.now(), j.expiraEm()).toSeconds());
    }

    public void limpar(String chave) {
        janelas.remove(chave);
    }

    /** Remove janelas vencidas para o mapa não crescer sem limite. */
    @Scheduled(fixedRate = 600_000)
    public void removerVencidas() {
        Instant agora = Instant.now();
        janelas.values().removeIf(j -> !agora.isBefore(j.expiraEm()));
    }
}
