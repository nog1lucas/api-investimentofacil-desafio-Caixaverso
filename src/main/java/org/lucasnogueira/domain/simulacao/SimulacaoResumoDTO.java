package org.lucasnogueira.domain.simulacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO representando um resumo de simulação para listagem
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimulacaoResumoDTO {

    @JsonProperty("id")
    @NotNull(message = "ID da simulação é obrigatório")
    @Positive(message = "ID da simulação deve ser positivo")
    private Long id;

    @JsonProperty("clienteId")
    @NotNull(message = "ID do cliente é obrigatório")
    @Positive(message = "ID da simulação deve ser positivo")
    private Long clienteId;

    @JsonProperty("produto")
    @NotBlank(message = "Produto é obrigatório")
    private String produto;

    @JsonProperty("valorTotalParcelas")
    @NotNull(message = "Valor total das parcelasé obrigatório")
    @PositiveOrZero(message = "Valor total das parcelas deve ser positivo ou zero")
    private BigDecimal valorInvestido;

    @JsonProperty("valorTotalParcelas")
    @NotNull(message = "Valor total das parcelasé obrigatório")
    @PositiveOrZero(message = "Valor total das parcelas deve ser positivo ou zero")
    private BigDecimal valorFinal;

    @JsonProperty("prazoMeses")
    @NotNull(message = "{solicita_simulacao_emprestimo_prazo_nulo}")
    @Positive(message = "{solicita_simulacao_emprestimo_prazo_menor_que_zero}")
    private Integer prazoMeses;

    @JsonProperty("dataSimulacao")
    @NotBlank(message = "Data da simulação é obrigatória")
    private String dataSimulacao;
}