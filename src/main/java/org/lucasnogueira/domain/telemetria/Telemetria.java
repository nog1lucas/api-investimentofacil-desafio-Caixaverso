package org.lucasnogueira.domain.telemetria;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Telemetria {

    private Long id;

    private String nomeServico;

    private LocalDate dataReferencia;

    private Long quantidadeChamadas;

    private Long quantidadeChamadasSucesso;

    private Long quantidadeChamadasErro;

    private BigDecimal tempoRespostaMs;

    private LocalDateTime tsCriacao;

    private LocalDateTime tsAtualizacao;

    /**
     * Calcula o tempo médio de resposta em milissegundos
     */
    public BigDecimal calcularTempoMedio() {
        if (quantidadeChamadas == 0) {
            return BigDecimal.ZERO;
        }
        return tempoRespostaMs.divide(BigDecimal.valueOf(quantidadeChamadas), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calcula o percentual de sucesso
     */
    public BigDecimal calcularPercentualSucesso() {
        if (quantidadeChamadas == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(quantidadeChamadasSucesso)
                .divide(BigDecimal.valueOf(quantidadeChamadas), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
