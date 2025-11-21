package org.lucasnogueira.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO representando um resumo de simulação para listagem
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoSimulacaoResponseDTO {

    @JsonProperty("id")
    @NotNull(message = "ID da simulação é obrigatório")
    @Positive(message = "ID da simulação deve ser positivo")
    private Long id;

    @JsonProperty("clienteId")
    @NotNull(message = "ID do cliente é obrigatório")
    @Positive(message = "ID da simulação deve ser positivo")
    private Integer clienteId;

    @JsonProperty("produto")
    @NotBlank(message = "Produto é obrigatório")
    private String produto;

    @JsonProperty("valorInvestido")
    @NotNull(message = "Valor Investido é obrigatório")
    @PositiveOrZero(message = "Valor Investido deve ser positivo ou zero")
    private BigDecimal valorInvestido;

    @JsonProperty("valorFinal")
    @NotNull(message = "valorFinal é obrigatório")
    @PositiveOrZero(message = "Valor Final deve ser positivo ou zero")
    private BigDecimal valorFinal;

    @JsonProperty("prazoMeses")
    @NotNull(message = "{solicita_simulacao_emprestimo_prazo_nulo}")
    @Positive(message = "{solicita_simulacao_emprestimo_prazo_menor_que_zero}")
    private Integer prazoMeses;

    @JsonProperty("dataSimulacao")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @NotBlank(message = "Data da simulação é obrigatória")
    private OffsetDateTime dataSimulacao;
}