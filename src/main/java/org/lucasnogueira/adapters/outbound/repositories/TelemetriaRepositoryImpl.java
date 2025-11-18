package org.lucasnogueira.adapters.outbound.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lucasnogueira.adapters.outbound.entities.JpaTelemetriaEntity;
import org.lucasnogueira.domain.telemetria.Telemetria;
import org.lucasnogueira.domain.telemetria.TelemetriaRepository;
import org.lucasnogueira.utils.mappers.TelemetriaMapper;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class TelemetriaRepositoryImpl implements TelemetriaRepository {

    @Inject
    JpaTelemetriaRepository jpaTelemetriaRepository;

    @Override
    public Telemetria persist(Telemetria telemetria) {
        return null;
    }

    @Override
    public Telemetria findById(Long id) {
        return null;
    }

    @Override
    public List<Telemetria> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<Telemetria> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataFim == null) {
            dataFim = LocalDate.now();
        }
        List<JpaTelemetriaEntity> entidades = jpaTelemetriaRepository.getEntityManager()
                .createQuery(
                        "SELECT t FROM JpaTelemetriaEntity t WHERE t.dataReferencia BETWEEN :dataInicio AND :dataFim",
                        JpaTelemetriaEntity.class)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .getResultList();

        return entidades.stream()
                .map(TelemetriaMapper.INSTANCE::jpaToDomain)
                .toList();
    }

    @Override
    public Telemetria buscarPorEndpointEData(String endpoint, LocalDate dataReferencia) {
        return null;
    }
}
