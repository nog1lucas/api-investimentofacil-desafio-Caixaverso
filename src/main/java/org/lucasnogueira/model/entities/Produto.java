package org.lucasnogueira.model.entities;

import jakarta.persistence.*;
import lombok.*;

import org.lucasnogueira.model.enums.TipoPerfilRisco;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_PRODUTO")
    public Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "IC_TIPO_PERFIL_RISCO")
    private TipoPerfilRisco tipoPerfilRisco;

    @Column(name = "NO_PRODUTO", length = 100)
    public String nome;

    @Column(name = "IC_TIPO_PRODUTO", length = 20)
    public String tipo;

    @Column(name = "PC_TAXA_ANUAL_OFERECIDA", precision = 8, scale = 5)
    public BigDecimal taxaAnualOferecida;

    @Column(name = "NU_LIQUIDEZ_DIAS")
    public Integer liquidezDias;

    @Column(name = "NO_RISCO", length = 20)
    private String risco;

}
