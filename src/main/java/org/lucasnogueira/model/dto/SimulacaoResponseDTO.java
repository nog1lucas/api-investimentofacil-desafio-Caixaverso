package org.lucasnogueira.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * DTO para retorno da simulação de investimento
 * Modelo de envelope de retorno para simulação
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SimulacaoResponseDTO {

    @JsonProperty("produtoValidado")
    @NotNull
    @NotEmpty
    @Valid
    private ProdutoValidadoDTO produtoValidado;

    @JsonProperty("resultadoSimulacao")
    @NotNull
    @NotEmpty
    @Valid
    private ResultadoSimulacaoDTO resultadoSimulacao;

    @JsonProperty("dataSimulacao")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @NotNull
    private OffsetDateTime dataSimulacao;
}