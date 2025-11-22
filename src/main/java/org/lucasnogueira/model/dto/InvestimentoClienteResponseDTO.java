package org.lucasnogueira.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestimentoClienteResponseDTO {

    private Long id;
    private String tipo;
    private BigDecimal valor;
    private BigDecimal rentabilidade;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private OffsetDateTime data;
}