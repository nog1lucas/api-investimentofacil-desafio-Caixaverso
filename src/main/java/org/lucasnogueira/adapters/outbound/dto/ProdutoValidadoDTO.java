package org.lucasnogueira.adapters.outbound.dto;

import lombok.*;

/**
 * DTO representando o produto validado para a simulação feita
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoValidadoDTO {
    private Long id;
    private String nome;
    private String tipo;
    private double rentabilidade;
    private String risco;
}

