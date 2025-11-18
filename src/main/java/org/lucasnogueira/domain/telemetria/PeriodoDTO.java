package org.lucasnogueira.domain.telemetria;

import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodoDTO {
    private String inicio;
    private String fim;

    public static PeriodoDTO from(LocalDate inicio, LocalDate fim) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        PeriodoDTO dto = new PeriodoDTO();
        dto.setInicio(inicio != null ? inicio.format(formatter) : null);
        dto.setFim(fim != null ? fim.format(formatter) : null);
        return dto;
    }
}