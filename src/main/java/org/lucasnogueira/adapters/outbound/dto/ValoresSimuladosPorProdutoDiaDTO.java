package org.lucasnogueira.adapters.outbound.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

/**
 * DTO representando os valores simulados por produto e dia
 * **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValoresSimuladosPorProdutoDiaDTO {

    private String produto;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private OffsetDateTime data;

    private Integer quantidadeSimulacoes;

    private BigDecimal mediaValorFinal;


    public OffsetDateTime getData() {
        return data;
    }

    public String getMediaValorFinal() {
        return mediaValorFinal != null ? mediaValorFinal.setScale(2, RoundingMode.HALF_UP).toString() : null;
    }

    public void setData(OffsetDateTime data) {
        this.data = data;
    }

    public void setMediaValorFinal(BigDecimal mediaValorFinal) {
        this.mediaValorFinal = mediaValorFinal;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public Integer getQuantidadeSimulacoes() {
        return quantidadeSimulacoes;
    }

    public void setQuantidadeSimulacoes(Integer quantidadeSimulacoes) {
        this.quantidadeSimulacoes = quantidadeSimulacoes;
    }

}
