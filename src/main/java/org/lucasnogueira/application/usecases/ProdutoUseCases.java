package org.lucasnogueira.application.usecases;

import org.lucasnogueira.enums.TipoPerfilRisco;

public interface ProdutoUseCases {
    Object listarProdutosRecomendadosPorPerfil(TipoPerfilRisco tipoPerfilRisco);
}
