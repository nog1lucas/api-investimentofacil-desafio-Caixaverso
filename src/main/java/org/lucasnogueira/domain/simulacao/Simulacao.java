package org.lucasnogueira.domain.simulacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Simulacao {

    public Long id;

    public Integer codigoCliente;

    public Long codigoProduto;

    public BigDecimal valorInvestido;

    public BigDecimal valorFinal;

    public Integer prazoMeses;

    public LocalDateTime dataSimulacao;

    public Integer pontuacao;

    public String perfilRisco;

    public Simulacao() {
    }

    public Simulacao(Integer codigoCliente, Long codigoProduto, LocalDateTime dataSimulacao, Long id, String perfilRisco, Integer pontuacao, Integer prazoMeses, BigDecimal valorFinal, BigDecimal valorInvestido) {
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

    public LocalDateTime getDataSimulacao() {
        return dataSimulacao;
    }

    public void setDataSimulacao(LocalDateTime dataSimulacao) {
        this.dataSimulacao = dataSimulacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPerfilRisco() {
        return perfilRisco;
    }

    public void setPerfilRisco(String perfilRisco) {
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
