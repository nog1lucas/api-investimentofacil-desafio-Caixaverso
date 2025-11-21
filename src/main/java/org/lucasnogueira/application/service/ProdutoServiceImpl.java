package org.lucasnogueira.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lucasnogueira.adapters.outbound.dto.ProdutoRecomendadoResponse;
import org.lucasnogueira.application.usecases.ProdutoUseCases;
import org.lucasnogueira.domain.produto.ProdutoRepository;
import org.lucasnogueira.domain.simulacao.SimulacaoRepository;
import org.lucasnogueira.enums.TipoPerfilRisco;

import java.util.List;

@ApplicationScoped
public class ProdutoServiceImpl implements ProdutoUseCases {

    @Inject
    ProdutoRepository produtoRepository;

    @Override
    public List<ProdutoRecomendadoResponse> listarProdutosRecomendadosPorPerfil(TipoPerfilRisco tipoPerfilRisco) {
        return produtoRepository.listarProdutosRecomendadosPorPerfil(tipoPerfilRisco);
    }
}
