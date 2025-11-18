package org.lucasnogueira.domain.produto;

import java.util.List;

public interface ProdutoRepository {

    Produto persist(Produto produto);

    Produto findById(Long id);

    List<Produto> findAll();

    void deleteById(Long id);
}
