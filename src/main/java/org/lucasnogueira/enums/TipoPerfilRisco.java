package org.lucasnogueira.enums;

/**
 * Enum para tipos de perfil de risco
 */
public enum TipoPerfilRisco {

    CONSERVADOR("Conservador", "Perfil de baixa movimentação, foco em liquidez"),
    MODERADO("Moderado", "Perfil de equilíbrio entre liquidez e rentabilidade"),
    AGRESSIVO("Agressivo", "Perfil de busca por alta rentabilidade, maior risco");

    private final String nome;
    private final String descricao;

    TipoPerfilRisco(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return nome;
    }
}