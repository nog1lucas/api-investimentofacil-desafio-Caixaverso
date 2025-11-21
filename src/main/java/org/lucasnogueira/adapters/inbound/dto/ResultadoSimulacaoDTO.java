package org.lucasnogueira.adapters.inbound.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO representando o resultado de uma simulação de investimento
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoSimulacaoDTO {
    private BigDecimal valorFinal ;
    private Double rentabilidadeEfetiva;
    private Integer prazoMeses;
}