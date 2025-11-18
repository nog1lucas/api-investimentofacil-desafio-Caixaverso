package org.lucasnogueira.domain.simulacao;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO representando os valores simulados por produto e dia
 * **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValoresSimuladosPorProdutoDiaDTO {
    private String produto;
    private String data;
    private Integer quantidadeSimulacoes;
    private BigDecimal mediaValorFinal;
}
