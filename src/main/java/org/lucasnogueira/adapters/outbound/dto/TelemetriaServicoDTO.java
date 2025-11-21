package org.lucasnogueira.adapters.outbound.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetriaServicoDTO {
    private String nome;
    private int quantidadeChamadas;
    private int mediaTempoRespostaMs;
    private BigDecimal percentualSucesso;
}