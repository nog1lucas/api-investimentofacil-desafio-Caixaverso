package org.lucasnogueira.model.dto;

public class ProdutoRecomendadoResponse {
    private Long id;
    private String nome;
    private String tipo;
    private Double rentabilidade;
    private String risco;

    public ProdutoRecomendadoResponse(Long id, String nome, String tipo, Double rentabilidade, String risco) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.rentabilidade = rentabilidade;
        this.risco = risco;
    }

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getRentabilidade() { return rentabilidade; }
    public void setRentabilidade(Double rentabilidade) { this.rentabilidade = rentabilidade; }

    public String getRisco() { return risco; }
    public void setRisco(String risco) { this.risco = risco; }
}