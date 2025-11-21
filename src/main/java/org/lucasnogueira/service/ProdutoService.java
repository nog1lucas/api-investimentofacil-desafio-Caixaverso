package org.lucasnogueira.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lucasnogueira.model.dto.ProdutoRecomendadoResponse;
import org.lucasnogueira.repositories.ProdutoRepository;
import org.lucasnogueira.model.enums.TipoPerfilRisco;

import java.util.List;

@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoRepository produtoRepository;

    public List<ProdutoRecomendadoResponse> listarProdutosRecomendadosPorPerfil(TipoPerfilRisco tipoPerfilRisco) {
        return produtoRepository.listarProdutosRecomendadosPorPerfil(tipoPerfilRisco);
    }
}
