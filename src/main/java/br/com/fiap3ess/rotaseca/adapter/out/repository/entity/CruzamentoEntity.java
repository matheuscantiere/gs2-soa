package br.com.fiap3ess.rotaseca.adapter.out.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity(name = "Cruzamento")
@Table(name = "cruzamentos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class CruzamentoEntity {
    @Id
    private String id;
    private String nome;
    @Column(name = "nome_curto")
    private String nomeCurto;
    private String via;
    private String bairro;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean placeholder;
    private int ordem;
    private boolean ativo = true;
}
