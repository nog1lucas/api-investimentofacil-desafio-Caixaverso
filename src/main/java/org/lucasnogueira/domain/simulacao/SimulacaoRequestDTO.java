package org.lucasnogueira.domain.simulacao;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.StringJoiner;

/**
 * DTO para entrada de dados da simulação de empréstimo
 * Modelo de envelope para simulação
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimulacaoRequestDTO {


    @NotNull(message = "{solicita_simulacao_emprestimo_valor_desejado_nulo}")
    @Positive(message = "{solicita_simulacao_emprestimo_valor_desejado_menor_que_zero}")
    private Integer clienteId;

    @NotNull(message = "{solicita_simulacao_emprestimo_valor_desejado_nulo}")
    @Positive(message = "{solicita_simulacao_emprestimo_valor_desejado_menor_que_zero}")
    @Digits(integer = 12, fraction = 2, message = "{solicita_simulacao_emprestimo_valor_desejado_tipo_invalido}")
    private BigDecimal valor;

    @NotNull(message = "{solicita_simulacao_emprestimo_prazo_nulo}")
    @Positive(message = "{solicita_simulacao_emprestimo_prazo_menor_que_zero}")
    private Integer prazoMeses;

    @NotBlank(message = "{solicita_simulacao_emprestimo_prazo_nulo}")
    private String tipoProduto;

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{", "}");

        joiner.add("clienteId=" + clienteId);
        joiner.add("valor=" + valor);
        joiner.add("prazoMeses=" + prazoMeses);
        joiner.add("tipoProduto=" + tipoProduto);

        return joiner.toString();
    }

}