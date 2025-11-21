package org.lucasnogueira.domain.simulacao;

import org.lucasnogueira.adapters.outbound.dto.HistoricoSimulacaoResponseDTO;
import org.lucasnogueira.adapters.outbound.dto.ValoresSimuladosPorProdutoDiaDTO;
import org.lucasnogueira.enums.TipoPerfilRisco;

import java.util.List;

public interface SimulacaoRepository {

    void persist(Simulacao simulacao);

    Simulacao findById(Long id);

    List<Simulacao> findAll();

    List<HistoricoSimulacaoResponseDTO> listarSimulacoesPaginado(int pagina, int tamanhoPagina);

    long countAll();

    List<ValoresSimuladosPorProdutoDiaDTO> buscarValoresSimuladosPorProdutoEDia();

    Object buscarPerfilPorCliente(Long clienteId);

    Object buscaSimulacoesPorCliente(Long clienteId);
}
