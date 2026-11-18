package br.com.fiap3ess.rotaseca.shared.util;

import br.com.fiap3ess.rotaseca.exception.type.ParametroInvalidoException;

import java.math.BigDecimal;

/** Caixa geográfica no formato do GeoJSON: "minLon,minLat,maxLon,maxLat". */
public record Bbox(BigDecimal minLon, BigDecimal minLat, BigDecimal maxLon, BigDecimal maxLat) {
    public static Bbox parse(String texto) {
        String[] partes = texto == null ? new String[0] : texto.split(",");
        if (partes.length != 4) {
            throw new ParametroInvalidoException("bbox deve ser minLon,minLat,maxLon,maxLat.");
        }
        try {
            Bbox caixa = new Bbox(
                    new BigDecimal(partes[0].trim()), new BigDecimal(partes[1].trim()),
                    new BigDecimal(partes[2].trim()), new BigDecimal(partes[3].trim()));
            if (caixa.minLon.compareTo(caixa.maxLon) > 0 || caixa.minLat.compareTo(caixa.maxLat) > 0) {
                throw new ParametroInvalidoException("bbox com mínimo maior que o máximo.");
            }
            return caixa;
        } catch (NumberFormatException e) {
            throw new ParametroInvalidoException("bbox deve conter apenas números.");
        }
    }

    public boolean contem(BigDecimal latitude, BigDecimal longitude) {
        return latitude != null && longitude != null
                && latitude.compareTo(minLat) >= 0 && latitude.compareTo(maxLat) <= 0
                && longitude.compareTo(minLon) >= 0 && longitude.compareTo(maxLon) <= 0;
    }
}
