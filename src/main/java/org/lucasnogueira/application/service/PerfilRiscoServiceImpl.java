package org.lucasnogueira.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lucasnogueira.application.usecases.PerfilRiscoUseCases;
import org.lucasnogueira.domain.simulacao.SimulacaoRepository;

@ApplicationScoped
public class PerfilRiscoServiceImpl implements PerfilRiscoUseCases {

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Override
    public Object buscarPerfilRiscoUseCase(Long clienteId) {
        return simulacaoRepository.buscarPerfilPorCliente(clienteId);
    }
}
