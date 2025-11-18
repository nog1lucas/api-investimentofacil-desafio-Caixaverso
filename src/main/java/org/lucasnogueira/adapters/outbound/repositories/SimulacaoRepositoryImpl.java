package org.lucasnogueira.adapters.outbound.repositories;

import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lucasnogueira.adapters.outbound.entities.JpaSimulacaoEntity;
import org.lucasnogueira.domain.simulacao.Simulacao;
import org.lucasnogueira.domain.simulacao.SimulacaoRepository;
import org.lucasnogueira.domain.simulacao.ValoresSimuladosPorProdutoDiaDTO;
import org.lucasnogueira.enums.TipoPerfilRisco;
import org.lucasnogueira.utils.mappers.SimulacaoMapper;

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
    public List<Simulacao> findAllPaged(int pagina, int tamanhoPagina) {
        List<JpaSimulacaoEntity> entities = this.jpaSimulacaoRepository.findAll()
                .page(Page.of(pagina, tamanhoPagina))
                .list();

        return entities.stream()
                .map(SimulacaoMapper.INSTANCE::jpaToDomain)
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
                        row[1].toString(),
                        ((Number) row[2]).intValue(),
                        new java.math.BigDecimal(row[3].toString())
                ))
                .collect(Collectors.toList());
    }

    public Object[] buscarPerfilPorCliente(Long clienteId) {
        return (Object[]) jpaSimulacaoRepository.getEntityManager().createQuery(
                        "SELECT s.codigoCliente, s.perfilRisco, s.pontuacao " +
                                "FROM JpaSimulacaoEntity s " +
                                "WHERE s.codigoCliente = :clienteId " +
                                "ORDER BY s.dataSimulacao DESC"
                )
                .setParameter("clienteId", clienteId)
                .setMaxResults(1)
                .getSingleResult();
    }

    public List<Object[]> listarProdutosRecomendados(TipoPerfilRisco tipoPerfilRisco) {
        return jpaSimulacaoRepository.getEntityManager()
                .createQuery(
                        "SELECT p.id, p.nome, p.tipo, p.taxaAnualOferecida, p.risco " +
                                "FROM JpaSimulacaoEntity s " +
                                "JOIN JpaProdutoEntity p ON s.codigoProduto = p.id " +
                                "WHERE s.perfilRisco = :tipoPerfilRisco " +
                                "ORDER BY p.nome ASC",
                        Object[].class
                )
                .setParameter("tipoPerfilRisco", tipoPerfilRisco.name())
                .getResultList();
    }
}
