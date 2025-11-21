package org.lucasnogueira.adapters.outbound.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lucasnogueira.adapters.outbound.dto.ProdutoRecomendadoResponse;
import org.lucasnogueira.adapters.outbound.entities.JpaProdutoEntity;
import org.lucasnogueira.domain.produto.Produto;
import org.lucasnogueira.domain.produto.ProdutoRepository;
import org.lucasnogueira.enums.TipoPerfilRisco;
//import org.lucasnogueira.util.mappers.Mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProdutoRepositoryImpl implements ProdutoRepository {

    @Inject
    JpaProdutoRepository jpaProdutoRepository;

//    @Inject
//    private Mapper mapper;


    @Override
    public List<Produto> findAll() {
    return this.jpaProdutoRepository.findAll()
            .stream()
            .map(entity -> new Produto(
                    entity.getCustoTransacaoPct(),
                    entity.getEmissor(),
                    entity.getId(),
                    entity.getLiquidezDias(),
                    entity.getNome(),
                    entity.getRating(),
                    entity.getRisco(),
                    entity.getTaxaAnualOferecida(),
                    entity.getTipo(),
                    entity.getVolumeMedioDiario()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public List<ProdutoRecomendadoResponse> listarProdutosRecomendadosPorPerfil(TipoPerfilRisco tipoPerfilRisco) {
        List<Object[]> resultados = jpaProdutoRepository.getEntityManager()
                .createQuery(
                        "SELECT p.id, p.nome, p.tipo, p.taxaAnualOferecida, p.risco " +
                                "FROM JpaProdutoEntity p " +
                                "WHERE p.tipoPerfilRisco = :tipoPerfilRisco " +
                                "ORDER BY p.nome ASC",
                        Object[].class
                )
                .setParameter("tipoPerfilRisco", tipoPerfilRisco)
                .getResultList();

        return resultados.stream()
                .map(row -> new ProdutoRecomendadoResponse(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).doubleValue(),
                        (String) row[4]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findTiposDistintos() {
        return jpaProdutoRepository.getEntityManager()
                .createQuery(
                        "SELECT DISTINCT p.tipo " +
                                "FROM JpaProdutoEntity p " +
                                "ORDER BY p.tipo ASC",
                        String.class
                )
                .getResultList();
    }
}
