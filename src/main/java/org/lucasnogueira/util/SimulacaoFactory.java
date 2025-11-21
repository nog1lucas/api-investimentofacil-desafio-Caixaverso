package org.lucasnogueira.util;

import jakarta.enterprise.context.ApplicationScoped;
import org.lucasnogueira.model.dto.SimulacaoRequestDTO;
import org.lucasnogueira.model.ProdutoComScore;
import org.lucasnogueira.model.entities.Simulacao;
import org.lucasnogueira.model.enums.TipoPerfilRisco;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@ApplicationScoped
public class SimulacaoFactory {

    public Simulacao criar(ProdutoComScore melhorProduto, SimulacaoRequestDTO requestDTO,
                           TipoPerfilRisco perfil, OffsetDateTime dataSimulacao, BigDecimal valorFinal) {

        Simulacao simulacao = new Simulacao();
        simulacao.setCodigoCliente(requestDTO.getClienteId());
        simulacao.setCodigoProduto(melhorProduto.getProduto().getId());
        simulacao.setPrazoMeses(requestDTO.getPrazoMeses());
        simulacao.setValorFinal(valorFinal);
        simulacao.setValorInvestido(requestDTO.getValor());
        simulacao.setDataSimulacao(dataSimulacao);

        // Calcula score baseado no risco do produto
        int scoreProduto = converterRiscoParaScore(melhorProduto.getProduto().getRisco());

        simulacao.setPontuacao(scoreProduto);
        simulacao.setTipoPerfilRisco(perfil);

        return simulacao;
    }

    private int converterRiscoParaScore(String risco) {
        return switch (risco.toLowerCase()) {
            case "muito baixo" -> 20;
            case "baixo" -> 35;
            case "medio" -> 50;
            case "alto" -> 70;
            case "muito alto" -> 85;
            default -> 50;
        };
    }
}