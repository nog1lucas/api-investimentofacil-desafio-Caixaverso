package org.lucasnogueira.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;

/**
 * DTO de resposta para listagem paginada de simulações
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListagemSimulacoesResponseDTO {

    @JsonProperty("pagina")
    @NotNull(message = "Número da página é obrigatório")
    @Positive(message = "Número da página deve ser positivo")
    private Integer pagina;

    @JsonProperty("qtdRegistros")
    @NotNull(message = "Quantidade de registros é obrigatória")
    @PositiveOrZero(message = "Quantidade de registros deve ser positiva ou zero")
    private long qtdRegistros;

    @JsonProperty("qtdRegistrosPagina")
    @NotNull(message = "Quantidade de registros por página é obrigatória")
    @Positive(message = "Quantidade de registros por página deve ser positiva")
    private Integer qtdRegistrosPagina;

    @JsonProperty("registros")
    @NotNull(message = "Lista de registros é obrigatória")
    @NotEmpty(message = "Lista de registros não pode estar vazia")
    @Valid
    private List<HistoricoSimulacaoResponseDTO> registros;
}

