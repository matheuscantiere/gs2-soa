package br.com.fiap3ess.rotaseca.adapter.out.repository.entity;

import br.com.fiap3ess.rotaseca.shared.vo.enumeration.FonteAlagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "Alagamento")
@Table(name = "alagamentos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class AlagamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cruzamento_id")
    private String cruzamentoId;
    @Column(name = "registrado_em")
    private LocalDateTime registradoEm;
    @Enumerated(EnumType.STRING)
    private FonteAlagamento fonte;
    private boolean intransitavel;
    private boolean ativo = true;
}
