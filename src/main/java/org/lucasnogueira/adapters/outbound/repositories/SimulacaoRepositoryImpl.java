package org.lucasnogueira.adapters.outbound.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lucasnogueira.adapters.inbound.dto.PerfilRiscoResponseDto;
import org.lucasnogueira.adapters.outbound.dto.HistoricoSimulacaoResponseDTO;
import org.lucasnogueira.adapters.outbound.dto.ValoresSimuladosPorProdutoDiaDTO;
import org.lucasnogueira.adapters.outbound.entities.JpaSimulacaoEntity;
import org.lucasnogueira.domain.simulacao.*;
import org.lucasnogueira.enums.TipoPerfilRisco;
import org.lucasnogueira.utils.mappers.SimulacaoMapper;
import org.lucasnogueira.utils.mappers.SimulacaoResumoMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SimulacaoRepositoryImpl implements SimulacaoRepository {

    @Inject
    JpaSimulacaoRepository jpaSimulacaoRepository;

    @Override
    public void persist(Simulacao simulacao) {
        JpaSimulacaoEntity simulacaoEntity = new JpaSimulacaoEntity(simulacao);
        this.jpaSimulacaoRepository.persist(simulacaoEntity);
    }

    @Override
    public Simulacao findById(Long id) {
//        Optional<JpaProdutoEntity> produtoEntity = this.jpaProdutoRepository.findById(id);
//        return produtoEntity.map(entity -> new Produto(entity.getId(), entity.getNome(), entity.getPreco())).orElse(null);
        return null;
    }

    @Override
    public List<Simulacao> findAll() {
        List<JpaSimulacaoEntity> entities = this.jpaSimulacaoRepository.findAll().list();
        return entities.stream()
                .map(SimulacaoMapper.INSTANCE::jpaToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<HistoricoSimulacaoResponseDTO> listarSimulacoesPaginado(int pagina, int tamanhoPagina) {
        List<Object[]> resultados = jpaSimulacaoRepository.getEntityManager()
                .createQuery(
                        "SELECT s.id, s.codigoCliente, p.nome, s.valorInvestido, s.valorFinal, s.prazoMeses, s.dataSimulacao " +
                                "FROM JpaSimulacaoEntity s " +
                                "JOIN JpaProdutoEntity p ON s.codigoProduto = p.id " +
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

    @Override
    public long countAll() {
       return this.jpaSimulacaoRepository.count();
    }

    public List<ValoresSimuladosPorProdutoDiaDTO> buscarValoresSimuladosPorProdutoEDia() {
        List<Object[]> resultados = jpaSimulacaoRepository.getEntityManager().createNativeQuery(
                "SELECT p.NO_PRODUTO, MAX(s.TS_CRIACAO_SIMULACAO), COUNT(*), AVG(s.VR_FINAL) " +
                        "FROM SIMULACAO s " +
                        "JOIN PRODUTO p ON s.CO_PRODUTO = p.CO_PRODUTO " +
                        "GROUP BY p.NO_PRODUTO "
        ).getResultList();

        return resultados.stream()
                .map(row -> new ValoresSimuladosPorProdutoDiaDTO(
                        (String) row[0],
                        (OffsetDateTime) row[1],
                        ((Number) row[2]).intValue(),
                        new java.math.BigDecimal(row[3].toString())
                ))
                .collect(Collectors.toList());
    }

    public PerfilRiscoResponseDto buscarPerfilPorCliente(Long clienteId) {
        Object[] resultado = (Object[]) jpaSimulacaoRepository.getEntityManager()
                .createQuery(
                        "SELECT s.codigoCliente, s.perfilRisco, AVG(s.pontuacao) " +
                                "FROM JpaSimulacaoEntity s " +
                                "WHERE s.codigoCliente = :clienteId " +
                                "ORDER BY s.dataSimulacao DESC"
                )
                .setParameter("clienteId", clienteId)
                .setMaxResults(1)
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

    @Override
    public Object buscaSimulacoesPorCliente(Long clienteId) {
       return jpaSimulacaoRepository.getEntityManager()
                .createQuery(
                        "SELECT s.id, p.tipo, s.valorFinal, p.taxaAnualOferecida, p.risco " +
                                "FROM JpaSimulacaoEntity s " +
                                "JOIN JpaProdutoEntity p ON s.codigoProduto = p.id " +
                                "WHERE s.codigoCliente = :clienteId " +
                                "ORDER BY s.dataSimulacao ASC",
                        Object[].class
                )
                .setParameter("clienteId", clienteId)
                .getResultList();
    }
}
