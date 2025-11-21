package org.lucasnogueira.domain.produto;

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