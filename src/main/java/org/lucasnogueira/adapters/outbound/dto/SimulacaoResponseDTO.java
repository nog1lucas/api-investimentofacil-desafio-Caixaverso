package org.lucasnogueira.adapters.outbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.lucasnogueira.adapters.inbound.dto.ResultadoSimulacaoDTO;

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
    @NotNull
    @Size(max = 200)
    private OffsetDateTime dataSimulacao;
}