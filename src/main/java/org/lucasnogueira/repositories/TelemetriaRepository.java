package org.lucasnogueira.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.lucasnogueira.model.entities.Telemetria;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class TelemetriaRepository implements PanacheRepository<Telemetria> {

    @Inject
    private EntityManager entityManager;

    @Transactional
    public void persist(Telemetria telemetria) {
        try {
            if (telemetria.getId() == null) {
                entityManager.persist(telemetria);
                log.info("Nova métrica persistida para endpoint: {} na data: {}",
                        telemetria.getNomeServico(), telemetria.getDataReferencia());
            } else {
                telemetria = entityManager.merge(telemetria);
                log.info("Métrica atualizada para endpoint: {} na data: {}",
                        telemetria.getNomeServico(), telemetria.getDataReferencia());
            }
        } catch (Exception e) {
            log.error("Erro ao salvar métrica para endpoint: {} na data: {}",
                    telemetria.getNomeServico(), telemetria.getDataReferencia(), e);
            throw e;
        }
    }

    public List<Telemetria> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        log.info("[TELEMETRIA] - Buscando por período: {} até {}", dataInicio, dataFim);

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

    public Optional<Telemetria> buscarPorEndpointEData(String nomeEndpoint, LocalDate dataReferencia) {
        log.info("[TELEMETRIA] - Buscando endpoint: {} na data: {}", nomeEndpoint, dataReferencia);

        try {
            TypedQuery<Telemetria> query = entityManager
                    .createQuery(
                            "SELECT t FROM Telemetria t WHERE t.nomeServico = :endpoint AND t.dataReferencia = :data",
                            Telemetria.class
                    );
            query.setParameter("endpoint", nomeEndpoint);
            query.setParameter("data", dataReferencia);

            List<Telemetria> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                log.debug("[TELEMETRIA] - Nenhuma telemetria encontrada para endpoint: {} na data: {}",
                        nomeEndpoint, dataReferencia);
                return Optional.empty();
            }

            log.debug("[TELEMETRIA] - Telemetria encontrada para endpoint: {} na data: {}",
                    nomeEndpoint, dataReferencia);
            return Optional.of(resultados.get(0));

        } catch (Exception e) {
            log.error("[TELEMETRIA] - Erro ao buscar métrica para endpoint: {} na data: {}",
                    nomeEndpoint, dataReferencia, e);
            return Optional.empty();
        }
    }

    private List<Telemetria> buscarEntreDatas(LocalDate dataInicio, LocalDate dataFim) {
        return entityManager
                .createQuery(
                        "SELECT t FROM Telemetria t WHERE t.dataReferencia BETWEEN :dataInicio AND :dataFim ORDER BY t.dataReferencia DESC",
                        Telemetria.class)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .getResultList();
    }

    private List<Telemetria> buscarAPartirDe(LocalDate dataInicio) {
        return entityManager
                .createQuery(
                        "SELECT t FROM Telemetria t WHERE t.dataReferencia >= :dataInicio ORDER BY t.dataReferencia DESC",
                        Telemetria.class)
                .setParameter("dataInicio", dataInicio)
                .getResultList();
    }

    private List<Telemetria> buscarAte(LocalDate dataFim) {
        return entityManager
                .createQuery(
                        "SELECT t FROM Telemetria t WHERE t.dataReferencia <= :dataFim ORDER BY t.dataReferencia DESC",
                        Telemetria.class)
                .setParameter("dataFim", dataFim)
                .getResultList();
    }

    private List<Telemetria> buscarTodos() {
        return entityManager
                .createQuery(
                        "SELECT t FROM Telemetria t ORDER BY t.dataReferencia DESC",
                        Telemetria.class)
                .getResultList();
    }

    public long count() {
        return entityManager
                .createQuery("SELECT COUNT(t) FROM Telemetria t", Long.class)
                .getSingleResult();
    }

    public List<Telemetria> buscarPaginado(int pagina, int tamanhoPagina) {
        log.info("[TELEMETRIA] - Buscando página {} com {} registros", pagina, tamanhoPagina);

        return entityManager
                .createQuery("SELECT t FROM Telemetria t ORDER BY t.dataReferencia DESC", Telemetria.class)
                .setFirstResult((pagina - 1) * tamanhoPagina)
                .setMaxResults(tamanhoPagina)
                .getResultList();
    }
}