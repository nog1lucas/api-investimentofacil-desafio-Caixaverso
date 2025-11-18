package org.lucasnogueira.adapters.outbound.entities;

import jakarta.persistence.*;
import lombok.*;
import org.lucasnogueira.domain.produto.Produto;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JpaProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_PRODUTO")
    public Long id;

    @Column(name = "NO_PRODUTO", length = 100)
    public String nome;

    @Column(name = "IC_TIPO_PRODUTO", length = 20)
    public String tipo;

    @Column(name = "PC_TAXA_ANUAL_OFERECIDA", precision = 8, scale = 5)
    public BigDecimal taxaAnualOferecida;

    @Column(name = "NU_LIQUIDEZ_DIAS")
    public Integer liquidezDias;

    @Column(name = "PC_CUSTO_TRANSACAO", precision = 8, scale = 5)
    public BigDecimal custoTransacaoPct;

    @Column(name = "NU_VOLUME_MEDIO_DIARIO")
    public Integer volumeMedioDiario;

    @Column(name = "NO_EMISSOR", length = 100)
    public String emissor;

    @Column(name = "NO_RATING", length = 10)
    public String rating;

    @Column(name = "NO_RISCO", length = 20)
    private String risco;

    public JpaProdutoEntity(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.tipo = produto.getTipo();
        this.taxaAnualOferecida = produto.getTaxaAnualOferecida();
        this.liquidezDias = produto.getLiquidezDias();
        this.custoTransacaoPct = produto.getCustoTransacaoPct();
        this.volumeMedioDiario = produto.getVolumeMedioDiario();
        this.emissor = produto.getEmissor();
        this.rating = produto.getRating();
    }
}
