package org.lucasnogueira.model;

import org.lucasnogueira.model.entities.Produto;

public class ProdutoComScore {
    private final Produto produto;
    private final double score;

    public ProdutoComScore(Produto produto, double score) {
        this.produto = produto;
        this.score = score;
    }

    public Produto getProduto() { return produto; }
    public double getScore() { return score; }
}