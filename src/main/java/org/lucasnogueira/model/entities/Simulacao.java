package org.lucasnogueira.model.entities;

import jakarta.persistence.*;
import lombok.*;

import org.lucasnogueira.model.enums.TipoPerfilRisco;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "SIMULACAO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_SIMULACAO")
    public Long id;

    @Column(name = "CO_ClIENTE")
    public Integer codigoCliente;

    @Column(name = "CO_PRODUTO")
    public Long codigoProduto;

    @Column(name = "NO_RISCO", length = 20)
    private String risco;

    @Column(name = "VR_INVESTIDO", precision = 18, scale = 2, nullable = true)
    public BigDecimal valorInvestido;

    @Column(name = "VR_FINAL", precision = 18, scale = 2, nullable = true)
    public BigDecimal valorFinal;

    @Column(name = "NU_PRAZO_MESES", nullable = true)
    public Integer prazoMeses;

    @Column(name = "TS_CRIACAO_SIMULACAO")
    public OffsetDateTime dataSimulacao;

    @Column(name = "NU_PONTUACAO")
    public Integer pontuacao;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "IC_TIPO_PERFIL_RISCO")
    private TipoPerfilRisco tipoPerfilRisco;

    @Column(name = "PC_RENTABILIDADE_EFETIVA", precision = 5, scale = 4, nullable = true)
    public BigDecimal rentabilidadeEfetiva;

}
