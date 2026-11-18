package br.com.fiap3ess.rotaseca.application.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Cruzamento coberto pelo estudo (equivale ao "Trecho" da proposta). O id é um slug fixo
 * (ex.: "nu-jaime") porque o app mobile referencia os cruzamentos por esse id.
 */
@Getter
@AllArgsConstructor
public class Cruzamento {
    private String id;
    private String nome;
    private String nomeCurto;
    private String via;
    private String bairro;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean placeholder;
    private int ordem;
    private boolean ativo;

    public boolean temCoordenada() {
        return latitude != null && longitude != null;
    }

    public void atualizarInformacoes(
            String nome,
            String nomeCurto,
            String via,
            String bairro,
            BigDecimal latitude,
            BigDecimal longitude,
            Boolean placeholder,
            Integer ordem) {
        if (nome != null && !nome.isBlank()) {
            this.nome = nome;
        }
        if (nomeCurto != null && !nomeCurto.isBlank()) {
            this.nomeCurto = nomeCurto;
        }
        if (via != null && !via.isBlank()) {
            this.via = via;
        }
        if (bairro != null && !bairro.isBlank()) {
            this.bairro = bairro;
        }
        if (latitude != null) {
            this.latitude = latitude;
        }
        if (longitude != null) {
            this.longitude = longitude;
        }
        if (placeholder != null) {
            this.placeholder = placeholder;
        }
        if (ordem != null) {
            this.ordem = ordem;
        }
    }

    public void excluir() {
        this.ativo = false;
    }
}
