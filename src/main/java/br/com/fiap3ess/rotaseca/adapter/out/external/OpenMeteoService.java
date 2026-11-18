package br.com.fiap3ess.rotaseca.adapter.out.external;

import br.com.fiap3ess.rotaseca.application.core.domain.PrevisaoChuva;
import br.com.fiap3ess.rotaseca.application.port.out.ProvedorChuva;
import br.com.fiap3ess.rotaseca.exception.type.ServicoExternoException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Request;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Consumo da API pública do Open-Meteo (sem chave), no mesmo padrão do ViaCEPService
 * da aula (fluent-hc + gson). Os valores de precipitação vêm somados por intervalo
 * (15 min = 900 s); a intensidade em mm/h é o valor x (3600 / intervalo).
 */
@Component
public class OpenMeteoService implements ProvedorChuva {
    private static final String URL = "https://api.open-meteo.com/v1/forecast"
            + "?latitude=%s&longitude=%s"
            + "&current=precipitation"
            + "&minutely_15=precipitation"
            + "&forecast_minutely_15=8"
            + "&timezone=America%%2FSao_Paulo";
    private static final int INTERVALO_15_MIN = 900;

    @Override
    public PrevisaoChuva consultar(BigDecimal latitude, BigDecimal longitude) {
        String url = String.format(URL, latitude.toPlainString(), longitude.toPlainString());
        try {
            String jsonResponse = Request.Get(url)
                    .connectTimeout(10000)
                    .socketTimeout(10000)
                    .execute()
                    .returnContent()
                    .asString();
            return converter(JsonParser.parseString(jsonResponse).getAsJsonObject(), latitude, longitude);
        } catch (IOException | RuntimeException e) {
            throw new ServicoExternoException("Não foi possível consultar a chuva no Open-Meteo.", e);
        }
    }

    /** Separado para poder ser testado sem rede. */
    static PrevisaoChuva converter(JsonObject json, BigDecimal latitude, BigDecimal longitude) {
        JsonObject atual = json.getAsJsonObject("current");
        int intervalo = atual.has("interval") ? atual.get("interval").getAsInt() : INTERVALO_15_MIN;
        LocalDateTime medidoEm = LocalDateTime.parse(atual.get("time").getAsString());
        BigDecimal intensidadeAtual = paraMmPorHora(atual.get("precipitation"), intervalo);

        List<PrevisaoChuva.Ponto> prevista = new ArrayList<>();
        if (json.has("minutely_15")) {
            JsonObject minutely = json.getAsJsonObject("minutely_15");
            JsonArray horas = minutely.getAsJsonArray("time");
            JsonArray valores = minutely.getAsJsonArray("precipitation");
            for (int i = 0; i < horas.size(); i++) {
                prevista.add(new PrevisaoChuva.Ponto(
                        LocalDateTime.parse(horas.get(i).getAsString()),
                        paraMmPorHora(valores.get(i), INTERVALO_15_MIN)));
            }
        }
        return new PrevisaoChuva(latitude, longitude, medidoEm, intensidadeAtual, prevista, "Open-Meteo");
    }

    private static BigDecimal paraMmPorHora(JsonElement valor, int intervaloSegundos) {
        if (valor == null || valor.isJsonNull()) {
            return BigDecimal.ZERO;
        }
        return valor.getAsBigDecimal()
                .multiply(BigDecimal.valueOf(3600))
                .divide(BigDecimal.valueOf(intervaloSegundos), 2, RoundingMode.HALF_UP);
    }
}
