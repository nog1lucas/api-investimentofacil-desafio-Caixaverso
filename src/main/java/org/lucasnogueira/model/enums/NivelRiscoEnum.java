package org.lucasnogueira.model.enums;

public enum NivelRiscoEnum {
    MUITO_BAIXO(1.0, "muito baixo", "muito_baixo"),
    BAIXO(3.0, "baixo"),
    MEDIO(5.0, "medio", "médio"),
    ALTO(7.0, "alto"),
    MUITO_ALTO(9.0, "muito alto", "muito_alto");

    private final double valor;
    private final String[] aliases;

    NivelRiscoEnum(double valor, String... aliases) {
        this.valor = valor;
        this.aliases = aliases;
    }

    public double getValor() {
        return valor;
    }

    public static NivelRiscoEnum fromString(String rating) {
        if (rating == null || rating.trim().isEmpty()) {
            return MEDIO; // Padrão
        }

        String ratingNormalizado = rating.trim().toLowerCase()
                .replace("_", " ")  // Converte underscores em espaços
                .replaceAll("\\s+", " "); // Remove espaços extras

        for (NivelRiscoEnum nivel : values()) {
            for (String alias : nivel.aliases) {
                if (alias.equals(ratingNormalizado)) {
                    return nivel;
                }
            }
        }

        return MEDIO; // Valor padrão se não encontrar
    }
}