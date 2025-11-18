package org.lucasnogueira.adapters.outbound.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lucasnogueira.adapters.outbound.entities.JpaProdutoEntity;
import org.lucasnogueira.domain.produto.Produto;
import org.lucasnogueira.domain.produto.ProdutoRepository;
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
    public Produto persist(Produto produto) {
//        JpaProdutoEntity produtoEntity = new JpaProdutoEntity(produto);
//        this.jpaProdutoRepository.persist(produtoEntity);
//        return new Produto(produtoEntity.getId(), produtoEntity.getNome(), produtoEntity.getPreco());
        return null;
    }

    @Override
    public Produto findById(Long id) {
//        Optional<JpaProdutoEntity> produtoEntity = this.jpaProdutoRepository.findById(id);
//        return produtoEntity.map(entity -> new Produto(entity.getId(), entity.getNome(), entity.getPreco())).orElse(null);
        return null;
    }

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
                    entity.getTaxaAnualOferecida(),
                    entity.getTipo(),
                    entity.getVolumeMedioDiario()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        this.jpaProdutoRepository.deleteById(id);
    }
}
