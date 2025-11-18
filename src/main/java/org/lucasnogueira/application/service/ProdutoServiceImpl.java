package org.lucasnogueira.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lucasnogueira.application.usecases.ProdutoUseCases;
import org.lucasnogueira.domain.simulacao.SimulacaoRepository;
import org.lucasnogueira.enums.TipoPerfilRisco;

@ApplicationScoped
public class ProdutoServiceImpl implements ProdutoUseCases {

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Override
    public Object listarProdutosRecomendados(TipoPerfilRisco tipoPerfilRisco) {
        return simulacaoRepository.listarProdutosRecomendados(tipoPerfilRisco);
    }
}
