package org.lucasnogueira.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.lucasnogueira.model.dto.ProdutoRecomendadoResponse;
import org.lucasnogueira.model.entities.Produto;
import org.lucasnogueira.model.enums.TipoPerfilRisco;
//import org.lucasnogueira.util.mappers.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Produto> {

    @Inject
    EntityManager entityManager;

//    @Inject
//    private Mapper mapper;

    public List<ProdutoRecomendadoResponse> listarProdutosRecomendadosPorPerfil(TipoPerfilRisco tipoPerfilRisco) {
        List<Object[]> resultados = entityManager
                .createQuery(
                        "SELECT p.id, p.nome, p.tipo, p.taxaAnualOferecida, p.risco " +
                                "FROM Produto p " +
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

    public List<String> findTiposDistintos() {
        return entityManager
                .createQuery(
                        "SELECT DISTINCT p.tipo " +
                                "FROM Produto p " +
                                "ORDER BY p.tipo ASC",
                        String.class
                )
                .getResultList();
    }
}
