package org.lucasnogueira.domain.simulacao;

import org.lucasnogueira.enums.TipoPerfilRisco;

import java.util.List;

public interface SimulacaoRepository {

    void persist(Simulacao simulacao);

    Simulacao findById(Long id);

    List<Simulacao> findAll();

    List<Simulacao> findAllPaged(int pagina, int tamanhoPagina);

    long countAll();

    List<ValoresSimuladosPorProdutoDiaDTO> buscarValoresSimuladosPorProdutoEDia();

    Object buscarPerfilPorCliente(Long clienteId);

    Object listarProdutosRecomendados(TipoPerfilRisco tipoPerfilRisco);
}
