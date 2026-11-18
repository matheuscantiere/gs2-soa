package br.com.fiap3ess.rotaseca.adapter.out.repository.entity;

import br.com.fiap3ess.rotaseca.shared.vo.enumeration.OrigemReporte;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "Reporte")
@Table(name = "reportes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class ReporteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private OrigemReporte origem;
    @Column(name = "cruzamento_id")
    private String cruzamentoId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    @Column(name = "nivel_cm")
    private Integer nivelCm;
    private String descricao;
    @Column(name = "recebido_em")
    private LocalDateTime recebidoEm;
}
