package org.lucasnogueira.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lucasnogueira.model.dto.ProdutoRecomendadoResponse;
import org.lucasnogueira.model.enums.TipoPerfilRisco;
import org.lucasnogueira.repositories.ProdutoRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static io.smallrye.common.constraint.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    ProdutoRepository produtoRepository;

    @InjectMocks
    ProdutoService produtoService;

    @Test
    void testListarProdutosRecomendadosPorPerfil() {
        TipoPerfilRisco perfil = TipoPerfilRisco.CONSERVADOR;
        ProdutoRecomendadoResponse produto1 = new ProdutoRecomendadoResponse();
        ProdutoRecomendadoResponse produto2 = new ProdutoRecomendadoResponse();
        List<ProdutoRecomendadoResponse> produtos = Arrays.asList(produto1, produto2);

        when(produtoRepository.listarProdutosRecomendadosPorPerfil(perfil)).thenReturn(produtos);

        List<ProdutoRecomendadoResponse> resultado = produtoService.listarProdutosRecomendadosPorPerfil(perfil);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(produtoRepository, times(1)).listarProdutosRecomendadosPorPerfil(perfil);
    }
}

