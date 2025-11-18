package org.lucasnogueira.adapters.outbound.entities;

import jakarta.persistence.*;
import lombok.*;
import org.lucasnogueira.domain.simulacao.Simulacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SIMULACAO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JpaSimulacaoEntity {

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

    @Column(name = "VR_INVESTIDO", precision = 10, scale = 2, nullable = true)
    public BigDecimal valorInvestido;

    @Column(name = "VR_FINAL", precision = 10, scale = 2, nullable = true)
    public BigDecimal valorFinal;

    @Column(name = "NU_PRAZO_MESES", precision = 10, scale = 2, nullable = true)
    public Integer prazoMeses;

    @Column(name = "TS_CRIACAO_SIMULACAO")
    public LocalDateTime dataSimulacao;

    @Column(name = "NU_PONTUACAO")
    public Integer pontuacao;

    @Column(name = "PERFIL_RISCO")
    public String perfilRisco;

    public JpaSimulacaoEntity(Simulacao simulacao) {
        this.id = simulacao.getId();
        this.codigoCliente = simulacao.getCodigoCliente();
        this.codigoProduto = simulacao.getCodigoProduto();
        this.valorFinal = simulacao.getValorFinal();
        this.valorInvestido = simulacao.getValorInvestido();
        this.prazoMeses = simulacao.getPrazoMeses();
        this.dataSimulacao = simulacao.getDataSimulacao();
        this.pontuacao = simulacao.getPontuacao();
        this.perfilRisco = simulacao.getPerfilRisco();
    }
}
