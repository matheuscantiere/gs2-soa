package br.com.fiap3ess.rotaseca.adapter.out.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "LeituraChuva")
@Table(name = "leituras_chuva")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class LeituraChuvaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cruzamento_id")
    private String cruzamentoId;
    @Column(name = "medido_em")
    private LocalDateTime medidoEm;
    @Column(name = "intensidade_mm_h")
    private BigDecimal intensidadeMmH;
    private boolean prevista;
    private String fonte;
}
