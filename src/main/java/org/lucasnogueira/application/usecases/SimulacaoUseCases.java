package org.lucasnogueira.application.usecases;

import org.lucasnogueira.domain.simulacao.ListagemSimulacoesResponseDTO;
import org.lucasnogueira.domain.simulacao.SimulacaoRequestDTO;
import org.lucasnogueira.domain.simulacao.SimulacaoResponseDTO;

public interface SimulacaoUseCases {

    SimulacaoResponseDTO simularInvestimento(SimulacaoRequestDTO requestDTO);
    ListagemSimulacoesResponseDTO buscarHistoricoSimulacoes(Integer pagina, Integer tamanhoPagina);
    Object buscarValoresSimuladosPorProdutoEDia(Integer pagina, Integer tamanhoPagina);
}
