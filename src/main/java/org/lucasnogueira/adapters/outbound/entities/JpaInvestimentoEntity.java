package org.lucasnogueira.adapters.outbound.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "INVESTIMENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JpaInvestimentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_INVESTIMENTO")
    private Long id;

    @Column(name = "CO_ClIENTE")
    private Integer clienteId;

    @ManyToOne
    @JoinColumn(name = "CO_PRODUTO")
    private JpaProdutoEntity produto;

    @Column(name = "VR_VALOR", precision = 10, scale = 2, nullable = true)
    private BigDecimal valor;

    @Column(name = "RENTABILIDADE", precision = 8, scale = 5)
    private BigDecimal rentabilidade;

    @Column(name = "DATA")
    private LocalDate data;
}