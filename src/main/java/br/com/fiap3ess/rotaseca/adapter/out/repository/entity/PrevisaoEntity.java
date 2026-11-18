package br.com.fiap3ess.rotaseca.adapter.out.repository.entity;

import br.com.fiap3ess.rotaseca.shared.vo.enumeration.StatusRisco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "Previsao")
@Table(name = "previsoes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class PrevisaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cruzamento_id")
    private String cruzamentoId;
    @Column(name = "previsao_hora")
    private LocalDateTime previsaoHora;
    private BigDecimal estimativa;
    private BigDecimal limiar;
    @Enumerated(EnumType.STRING)
    private StatusRisco status;
    private String fonte;
}
