package org.lucasnogueira.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.lucasnogueira.model.dto.PerfilRiscoResponseDto;
import org.lucasnogueira.model.dto.HistoricoSimulacaoResponseDTO;
import org.lucasnogueira.model.dto.ValoresSimuladosPorProdutoDiaDTO;
import org.lucasnogueira.model.entities.Simulacao;
import org.lucasnogueira.model.enums.TipoPerfilRisco;
import org.lucasnogueira.util.mappers.SimulacaoResumoMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SimulacaoRepository implements PanacheRepository<Simulacao> {

    @Inject
    EntityManager entityManager;

    public List<HistoricoSimulacaoResponseDTO> listarSimulacoesPaginado(int pagina, int tamanhoPagina) {
        List<Object[]> resultados = entityManager
                .createQuery(
                        "SELECT s.id, s.codigoCliente, p.nome, s.valorInvestido, s.valorFinal, s.prazoMeses, s.dataSimulacao " +
                                "FROM Simulacao s " +
                                "JOIN Produto p ON s.codigoProduto = p.id " +
                                "ORDER BY s.dataSimulacao DESC",
                        Object[].class
                )
                .setFirstResult(pagina * tamanhoPagina)
                .setMaxResults(tamanhoPagina)
                .getResultList();

        return resultados.stream()
                .map(SimulacaoResumoMapper::fromObjectArray)
                .collect(Collectors.toList());
    }

    public List<ValoresSimuladosPorProdutoDiaDTO> buscarValoresSimuladosPorProdutoEDia(LocalDate dataReferencia) {
        OffsetDateTime inicioDia = dataReferencia.atStartOfDay().atOffset(java.time.ZoneOffset.UTC);
        OffsetDateTime fimDia = dataReferencia.plusDays(1).atStartOfDay().atOffset(java.time.ZoneOffset.UTC);

        List<Object[]> resultados = entityManager.createQuery(
                        "SELECT p.nome, :dataRef, COUNT(s), AVG(s.valorFinal) " +
                                "FROM Simulacao s " +
                                "JOIN Produto p ON s.codigoProduto = p.id " +
                                "WHERE s.dataSimulacao >= :inicioDia AND s.dataSimulacao < :fimDia " +
                                "GROUP BY p.nome",
                        Object[].class
                )
                .setParameter("inicioDia", inicioDia)
                .setParameter("fimDia", fimDia)
                .setParameter("dataRef", inicioDia)
                .getResultList();

        return resultados.stream()
                .map(row -> new ValoresSimuladosPorProdutoDiaDTO(
                        (String) row[0],
                        (java.time.OffsetDateTime) row[1],
                        ((Number) row[2]).intValue(),
                        java.math.BigDecimal.valueOf(((Number) row[3]).doubleValue())
                ))
                .collect(Collectors.toList());
    }

    public PerfilRiscoResponseDto buscarPerfilPorCliente(Long clienteId) {
        Object[] resultado = (Object[]) entityManager
                .createQuery(
                        "SELECT s.codigoCliente, s.tipoPerfilRisco, AVG(s.pontuacao) " +
                                "FROM Simulacao s " +
                                "WHERE s.codigoCliente = :clienteId " +
                                "ORDER BY s.dataSimulacao DESC"
                )
                .setParameter("clienteId", clienteId)
                .getSingleResult();

        Integer idCliente = ((Number) resultado[0]).intValue();
        TipoPerfilRisco perfilRisco = (TipoPerfilRisco) resultado[1];
        Double mediaPontuacao = (Double) resultado[2];

        return new PerfilRiscoResponseDto(
                idCliente,
                perfilRisco.getNome(),
                mediaPontuacao,
                perfilRisco.getDescricao()
        );
    }

    public List<Object[]> buscaSimulacoesPorCliente(Long clienteId) {
       return entityManager
                .createQuery(
                        "SELECT s.id, p.tipo, s.valorFinal, p.taxaAnualOferecida, s.dataSimulacao " +
                                "FROM Simulacao s " +
                                "JOIN Produto p ON s.codigoProduto = p.id " +
                                "WHERE s.codigoCliente = :clienteId " +
                                "ORDER BY s.dataSimulacao ASC",
                        Object[].class
                )
                .setParameter("clienteId", clienteId)
                .getResultList();
    }

}
