package org.lucasnogueira.domain.simulacao;

import org.lucasnogueira.enums.TipoPerfilRisco;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Simulacao {

    public Long id;

    public Integer codigoCliente;

    public Long codigoProduto;

    public BigDecimal valorInvestido;

    public BigDecimal valorFinal;

    public Integer prazoMeses;

    public OffsetDateTime dataSimulacao;

    public Integer pontuacao;

    public TipoPerfilRisco perfilRisco;

    public Simulacao() {
    }

    public Simulacao(Integer codigoCliente, Long codigoProduto, OffsetDateTime dataSimulacao, Long id, TipoPerfilRisco perfilRisco, Integer pontuacao, Integer prazoMeses, BigDecimal valorFinal, BigDecimal valorInvestido) {
        this.codigoCliente = codigoCliente;
        this.codigoProduto = codigoProduto;
        this.dataSimulacao = dataSimulacao;
        this.id = id;
        this.perfilRisco = perfilRisco;
        this.pontuacao = pontuacao;
        this.prazoMeses = prazoMeses;
        this.valorFinal = valorFinal;
        this.valorInvestido = valorInvestido;
    }

    public Integer getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(Integer codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Long getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(Long codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public OffsetDateTime getDataSimulacao() {
        return dataSimulacao;
    }

    public void setDataSimulacao(OffsetDateTime dataSimulacao) {
        this.dataSimulacao = dataSimulacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoPerfilRisco getPerfilRisco() {
        return perfilRisco;
    }

    public void setPerfilRisco(TipoPerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    public Integer getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Integer pontuacao) {
        this.pontuacao = pontuacao;
    }

    public Integer getPrazoMeses() {
        return prazoMeses;
    }

    public void setPrazoMeses(Integer prazoMeses) {
        this.prazoMeses = prazoMeses;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public BigDecimal getValorInvestido() {
        return valorInvestido;
    }

    public void setValorInvestido(BigDecimal valorInvestido) {
        this.valorInvestido = valorInvestido;
    }
}
