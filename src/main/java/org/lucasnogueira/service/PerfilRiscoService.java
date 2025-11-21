package org.lucasnogueira.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lucasnogueira.model.dto.PerfilRiscoResponseDto;
import org.lucasnogueira.model.enums.TipoPerfilRisco;
import org.lucasnogueira.repositories.SimulacaoRepository;

@ApplicationScoped
public class PerfilRiscoService {

    @Inject
    SimulacaoRepository simulacaoRepository;

    public PerfilRiscoResponseDto buscarPerfilRisco(Long clienteId) {
        PerfilRiscoResponseDto resultado = simulacaoRepository.buscarPerfilPorCliente(clienteId);

        // Mapeia a pontuação média para o perfil de risco
        TipoPerfilRisco perfilRisco = mapearPontuacaoParaPerfil(resultado.getPontuacao());

        return new PerfilRiscoResponseDto(
                resultado.getClienteId(),
                perfilRisco.getNome(),
                resultado.getPontuacao(),
                perfilRisco.getDescricao()
        );
    }

    private TipoPerfilRisco mapearPontuacaoParaPerfil(Double pontuacaoMedia) {
        if (pontuacaoMedia <= 32.5) {        // Até média entre baixo(35) e muito baixo(20)
            return TipoPerfilRisco.CONSERVADOR;
        } else if (pontuacaoMedia <= 60.0) { // Até média entre médio(50) e alto(70)
            return TipoPerfilRisco.MODERADO;
        } else {                             // Acima de 60 (tendendo para alto/muito alto)
            return TipoPerfilRisco.AGRESSIVO;
        }
    }
}