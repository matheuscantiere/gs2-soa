package br.com.fiap3ess.rotaseca.adapter.in.scheduler;

import br.com.fiap3ess.rotaseca.application.service.ChuvaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Busca a chuva no Open-Meteo para cada cruzamento a cada 15 minutos (desligado no perfil de teste). */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rotaseca.chuva.job.habilitado", havingValue = "true")
public class ColetaChuvaJob {
    private final ChuvaService chuvaService;

    @Scheduled(fixedRateString = "${rotaseca.chuva.job.intervalo-ms}", initialDelay = 30000)
    public void coletar() {
        int gravadas = chuvaService.coletarParaCruzamentos();
        log.info("Coleta de chuva concluída: {} leituras gravadas", gravadas);
    }
}
