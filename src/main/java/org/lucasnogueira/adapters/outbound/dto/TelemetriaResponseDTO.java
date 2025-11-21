package org.lucasnogueira.adapters.outbound.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetriaResponseDTO {
    private List<TelemetriaServicoDTO> servicos;
    private PeriodoDTO periodo;
}
