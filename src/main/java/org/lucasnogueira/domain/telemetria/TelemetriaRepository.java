package org.lucasnogueira.domain.telemetria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TelemetriaRepository {

    void persist(Telemetria telemetria);

    Telemetria findById(Long id);

    List<Telemetria> findAll();

    void deleteById(Long id);

    List<Telemetria> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    Optional<Telemetria> buscarPorEndpointEData(String nomeEndpoint, LocalDate dataReferencia);
}
