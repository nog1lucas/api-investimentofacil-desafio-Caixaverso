package org.lucasnogueira.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.lucasnogueira.model.dto.SimulacaoRequestDTO;

import org.lucasnogueira.model.entities.Produto;
import org.lucasnogueira.model.enums.NivelRiscoEnum;
import org.lucasnogueira.model.enums.TipoPerfilRisco;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ProdutoScoreCalculator {

    public double calcular(Produto produto, SimulacaoRequestDTO req, TipoPerfilRisco perfil, List<Produto> todosProdutos) {
        PesosScore pesos = obterPesosPorPerfil(perfil);

        double retornoMaximo = calcularRetornoMaximo(todosProdutos);
        double liquidezMinima = calcularLiquidezMinima(todosProdutos);
        double liquidezMaxima = calcularLiquidezMaxima(todosProdutos);

        double taxaBruta = produto.getTaxaAnualOferecida().doubleValue();
        double aliquotaIr = calcularAliquotaIr(req.getPrazoMeses() * 30, produto.getTipo());
        double taxaLiquida = taxaBruta * (1 - aliquotaIr);

        double retornoNorm = taxaLiquida / retornoMaximo;
        double liquidezNorm = normalizarLiquidez(produto.getLiquidezDias(), req.getPrazoMeses() * 30, liquidezMinima, liquidezMaxima);
        double riscoNorm = normalizarRisco(produto.getRisco(), perfil);

        double volumeNorm = normalizarVolume(req.getValor());
        double frequenciaNorm = normalizarFrequencia(produto, req.getPrazoMeses());

        double score = pesos.retorno() * retornoNorm +
                pesos.liquidez() * liquidezNorm +
                pesos.risco() * riscoNorm +
                0.10 * volumeNorm +
                0.10 * frequenciaNorm;

        return Math.max(0.0, Math.min(1.0, score));
    }

    private double normalizarVolume(BigDecimal valorInvestimento) {
        double valor = valorInvestimento.doubleValue();

        if (valor >= 100000) return 1.0;      // Alto volume
        if (valor >= 50000) return 0.8;       // Médio-alto
        if (valor >= 10000) return 0.6;       // Médio
        if (valor >= 1000) return 0.4;        // Baixo
        return 0.2;                           // Muito baixo
    }

    private double normalizarFrequencia(Produto produto, int prazoMeses) {
        // Clientes com prazos menores tendem a movimentar mais
        if (prazoMeses <= 6) {
            // Prazo curto = alta frequência esperada = prefere liquidez
            return produto.getLiquidezDias() <= 30 ? 1.0 : 0.3;
        } else if (prazoMeses <= 24) {
            // Prazo médio = frequência moderada
            return produto.getLiquidezDias() <= 90 ? 0.8 : 0.6;
        } else {
            // Prazo longo = baixa frequência = aceita menor liquidez
            return produto.getLiquidezDias() >= 90 ? 1.0 : 0.7;
        }
    }

    private PesosScore obterPesosPorPerfil(TipoPerfilRisco perfil) {
        return switch (perfil) {
            case CONSERVADOR -> new PesosScore(0.12, 0.36, 0.32);
            case MODERADO -> new PesosScore(0.24, 0.20, 0.36);
            default -> new PesosScore(0.32, 0.08, 0.40);
        };
    }

    private double calcularRetornoMaximo(List<Produto> produtos) {
        return produtos.stream()
                .mapToDouble(p -> p.getTaxaAnualOferecida().doubleValue())
                .max()
                .orElse(0.01);
    }

    private double calcularLiquidezMinima(List<Produto> produtos) {
        return produtos.stream()
                .mapToDouble(Produto::getLiquidezDias)
                .min()
                .orElse(0.0);
    }

    private double calcularLiquidezMaxima(List<Produto> produtos) {
        return produtos.stream()
                .mapToDouble(Produto::getLiquidezDias)
                .max()
                .orElse(1.0);
    }

    private double calcularAliquotaIr(int dias, String tipo) {
        if (tipo.equalsIgnoreCase("LCI") || tipo.equalsIgnoreCase("LCA")) return 0.0;
        if (tipo.equalsIgnoreCase("Ações")) return 0.15;
        if (dias <= 180) return 0.225;
        if (dias <= 360) return 0.20;
        if (dias <= 720) return 0.175;
        return 0.15;
    }

    private double normalizarLiquidez(double diasLiquidez, int prazoClienteDias, double liquidezMin, double liquidezMax) {
        if (diasLiquidez <= prazoClienteDias) return 1.0;
        if (liquidezMax <= liquidezMin) return 0.0;
        return Math.max(0, 1.0 - (diasLiquidez - prazoClienteDias) / (liquidezMax - liquidezMin));
    }

    private double normalizarRisco(String rating, TipoPerfilRisco perfil) {
        double nivelRisco = NivelRiscoEnum.fromString(rating).getValor();
        return switch (perfil) {
            case CONSERVADOR -> 1.0 - (nivelRisco - 1.0) / 9.0;
            case MODERADO -> 1.0 - Math.abs(5.0 - nivelRisco) / 5.0;
            default -> (nivelRisco - 1.0) / 9.0;
        };
    }

    private record PesosScore(double retorno, double liquidez, double risco) {}
}