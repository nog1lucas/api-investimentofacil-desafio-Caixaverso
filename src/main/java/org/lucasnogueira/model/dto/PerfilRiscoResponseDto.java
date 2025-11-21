package org.lucasnogueira.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PerfilRiscoResponseDto {

    @JsonProperty("clienteId")
    @NotNull
    private Integer clienteId;

    @JsonProperty("perfil")
    @NotNull
    private String perfil;

    @JsonProperty("pontuacao")
    @NotNull
    private Double pontuacao;

    @JsonProperty("descricao")
    @NotNull
    private String descricao;

}