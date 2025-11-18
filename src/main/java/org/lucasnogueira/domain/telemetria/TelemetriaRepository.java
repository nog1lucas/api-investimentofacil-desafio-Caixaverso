package org.lucasnogueira.domain.telemetria;

import java.time.LocalDate;
import java.util.List;

public interface TelemetriaRepository {

    Telemetria persist(Telemetria telemetria);

    Telemetria findById(Long id);

    List<Telemetria> findAll();

    void deleteById(Long id);

    List<Telemetria> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    Telemetria buscarPorEndpointEData(String endpoint, LocalDate dataReferencia);
}
