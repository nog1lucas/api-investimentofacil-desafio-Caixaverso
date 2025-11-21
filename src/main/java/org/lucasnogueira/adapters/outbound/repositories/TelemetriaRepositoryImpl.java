package org.lucasnogueira.adapters.outbound.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.lucasnogueira.adapters.outbound.entities.JpaTelemetriaEntity;
import org.lucasnogueira.domain.telemetria.Telemetria;
import org.lucasnogueira.domain.telemetria.TelemetriaRepository;
import org.lucasnogueira.utils.mappers.TelemetriaMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class TelemetriaRepositoryImpl implements TelemetriaRepository {

    @Inject
    JpaTelemetriaRepository jpaTelemetriaRepository;

    @Override
    @Transactional
    public void persist(Telemetria telemetria) {
//        jpaTelemetriaRepository.persist(
//                TelemetriaMapper.INSTANCE.domainToJpa(telemetria)
//        );
        JpaTelemetriaEntity entity = new JpaTelemetriaEntity(telemetria);
        jpaTelemetriaRepository.persist(entity);
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
        if (dataInicio != null && dataFim != null) {
            return buscarEntreDatas(dataInicio, dataFim);
        } else if (dataInicio != null) {
            return buscarAPartirDe(dataInicio);
        } else if (dataFim != null) {
            return buscarAte(dataFim);
        } else {
            return buscarTodos();
        }
    }

    private List<Telemetria> buscarEntreDatas(LocalDate dataInicio, LocalDate dataFim) {
        var entidades = jpaTelemetriaRepository.getEntityManager()
                .createQuery("SELECT t FROM JpaTelemetriaEntity t WHERE t.dataReferencia BETWEEN :dataInicio AND :dataFim", JpaTelemetriaEntity.class)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .getResultList();
        return entidades.stream().map(TelemetriaMapper.INSTANCE::jpaToDomain).toList();
    }

    private List<Telemetria> buscarAPartirDe(LocalDate dataInicio) {
        var entidades = jpaTelemetriaRepository.getEntityManager()
                .createQuery("SELECT t FROM JpaTelemetriaEntity t WHERE t.dataReferencia >= :dataInicio", JpaTelemetriaEntity.class)
                .setParameter("dataInicio", dataInicio)
                .getResultList();
        return entidades.stream().map(TelemetriaMapper.INSTANCE::jpaToDomain).toList();
    }

    private List<Telemetria> buscarAte(LocalDate dataFim) {
        var entidades = jpaTelemetriaRepository.getEntityManager()
                .createQuery("SELECT t FROM JpaTelemetriaEntity t WHERE t.dataReferencia <= :dataFim", JpaTelemetriaEntity.class)
                .setParameter("dataFim", dataFim)
                .getResultList();
        return entidades.stream().map(TelemetriaMapper.INSTANCE::jpaToDomain).toList();
    }

    private List<Telemetria> buscarTodos() {
        var entidades = jpaTelemetriaRepository.getEntityManager()
                .createQuery("SELECT t FROM JpaTelemetriaEntity t", JpaTelemetriaEntity.class)
                .getResultList();
        return entidades.stream().map(TelemetriaMapper.INSTANCE::jpaToDomain).toList();
    }

    @Override
    @Transactional
    public Optional<Telemetria> buscarPorEndpointEData(String nomeEndpoint, LocalDate dataReferencia) {
        try {
            TypedQuery<JpaTelemetriaEntity> query = jpaTelemetriaRepository.getEntityManager()
                    .createQuery(
                            "SELECT t FROM JpaTelemetriaEntity t WHERE t.nomeServico = :endpoint AND t.dataReferencia = :data",
                            JpaTelemetriaEntity.class
                    );
            query.setParameter("endpoint", nomeEndpoint);
            query.setParameter("data", dataReferencia);

            List<JpaTelemetriaEntity> resultados = query.getResultList();
            if (resultados.isEmpty()) {
                return Optional.empty();
            }
            Telemetria telemetria = TelemetriaMapper.INSTANCE.jpaToDomain(resultados.get(0));
            return Optional.of(telemetria);
        } catch (Exception e) {
            log.error("Erro ao buscar métrica para endpoint: {} na data: {}", nomeEndpoint, dataReferencia, e);
            return Optional.empty();
        }
    }
}
