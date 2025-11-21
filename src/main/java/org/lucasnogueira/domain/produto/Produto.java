package org.lucasnogueira.domain.produto;

import java.math.BigDecimal;

public class Produto {

    public Long id;

    public String nome;

    public String tipo;

    public BigDecimal taxaAnualOferecida;

    public Integer liquidezDias;

    public BigDecimal custoTransacaoPct;

    public Integer volumeMedioDiario;

    public String emissor;

    public String rating;

    private String risco;

    public Produto() {
    }

    public Produto(BigDecimal custoTransacaoPct, String emissor, Long id, Integer liquidezDias, String nome, String rating, String risco, BigDecimal taxaAnualOferecida, String tipo, Integer volumeMedioDiario) {
        this.custoTransacaoPct = custoTransacaoPct;
        this.emissor = emissor;
        this.id = id;
        this.liquidezDias = liquidezDias;
        this.nome = nome;
        this.rating = rating;
        this.risco = risco;
        this.taxaAnualOferecida = taxaAnualOferecida;
        this.tipo = tipo;
        this.volumeMedioDiario = volumeMedioDiario;
    }

    public BigDecimal getCustoTransacaoPct() {
        return custoTransacaoPct;
    }

    public void setCustoTransacaoPct(BigDecimal custoTransacaoPct) {
        this.custoTransacaoPct = custoTransacaoPct;
    }

    public String getEmissor() {
        return emissor;
    }

    public void setEmissor(String emissor) {
        this.emissor = emissor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLiquidezDias() {
        return liquidezDias;
    }

    public void setLiquidezDias(Integer liquidezDias) {
        this.liquidezDias = liquidezDias;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRisco() {
        return risco;
    }

    public void setRisco(String risco) {
        this.risco = risco;
    }

    public BigDecimal getTaxaAnualOferecida() {
        return taxaAnualOferecida;
    }

    public void setTaxaAnualOferecida(BigDecimal taxaAnualOferecida) {
        this.taxaAnualOferecida = taxaAnualOferecida;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getVolumeMedioDiario() {
        return volumeMedioDiario;
    }

    public void setVolumeMedioDiario(Integer volumeMedioDiario) {
        this.volumeMedioDiario = volumeMedioDiario;
    }
}