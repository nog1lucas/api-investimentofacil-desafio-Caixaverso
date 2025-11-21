package org.lucasnogueira.application.usecases;

import org.lucasnogueira.adapters.inbound.dto.SimulacaoRequestDTO;
import org.lucasnogueira.adapters.outbound.dto.ListagemSimulacoesResponseDTO;
import org.lucasnogueira.adapters.outbound.dto.SimulacaoResponseDTO;
import org.lucasnogueira.adapters.outbound.dto.ValoresSimuladosPorProdutoDiaDTO;

import java.util.List;

public interface SimulacaoUseCases {

    SimulacaoResponseDTO simularInvestimento(SimulacaoRequestDTO requestDTO);
    ListagemSimulacoesResponseDTO buscarHistoricoSimulacoes(Integer pagina, Integer tamanhoPagina);
    List<ValoresSimuladosPorProdutoDiaDTO> buscarValoresSimuladosPorProdutoEDia(Integer pagina, Integer tamanhoPagina);
    Object buscaSimulacoesPorCliente(Long clienteId);
}
