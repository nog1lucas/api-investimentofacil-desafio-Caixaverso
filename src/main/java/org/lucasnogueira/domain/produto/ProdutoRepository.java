package org.lucasnogueira.domain.produto;

import org.lucasnogueira.adapters.outbound.dto.ProdutoRecomendadoResponse;
import org.lucasnogueira.enums.TipoPerfilRisco;

import java.util.List;

public interface ProdutoRepository {

    List<Produto> findAll();

    List<ProdutoRecomendadoResponse> listarProdutosRecomendadosPorPerfil(TipoPerfilRisco tipoPerfilRisco);

    List<String> findTiposDistintos();
}
