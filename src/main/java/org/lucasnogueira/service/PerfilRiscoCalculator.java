package org.lucasnogueira.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.lucasnogueira.model.dto.SimulacaoRequestDTO;
import org.lucasnogueira.model.enums.TipoPerfilRisco;

import java.math.BigDecimal;

@ApplicationScoped
public class PerfilRiscoCalculator {

    public TipoPerfilRisco calcular(SimulacaoRequestDTO requestDTO) {
        double score = calcularScoreTotal(requestDTO);
        return determinarPerfil(score);
    }

    private double calcularScoreTotal(SimulacaoRequestDTO requestDTO) {
        BigDecimal valor = requestDTO.getValor();
        int prazo = requestDTO.getPrazoMeses();

        // Score baseado em valor (usando interpolação)
        double scoreValor = calcularScoreValor(valor);

        // Score baseado em prazo (usando interpolação)
        double scorePrazo = calcularScorePrazo(prazo);

        return scoreValor + scorePrazo;
    }

    private double calcularScoreValor(BigDecimal valor) {
        double v = valor.doubleValue();

        // Interpolação linear entre pontos-chave
        if (v <= 20000) return -1.0;                    // <= 20K = -1
        if (v <= 50000) return interpolate(-1.0, 0.5, v, 20000, 50000);   // 20K-50K = -1 a +0.5
        if (v <= 100000) return interpolate(0.5, 1.5, v, 50000, 100000);  // 50K-100K = +0.5 a +1.5
        if (v <= 300000) return interpolate(1.5, 2.5, v, 100000, 300000); // 100K-300K = +1.5 a +2.5
        if (v <= 1000000) return interpolate(2.5, 3.5, v, 300000, 1000000); // 300K-1M = +2.5 a +3.5
        if (v <= 5000000) return interpolate(3.5, 4.5, v, 1000000, 5000000); // 1M-5M = +3.5 a +4.5
        return 5.0;                                     // > 5M = +5
    }

    private double calcularScorePrazo(int prazo) {
        // Interpolação linear para prazos
        if (prazo <= 3) return -3.0;                    // <= 3 meses = -3
        if (prazo <= 6) return interpolate(-3.0, -2.0, prazo, 3, 6);     // 3-6 meses = -3 a -2
        if (prazo <= 12) return interpolate(-2.0, -0.5, prazo, 6, 12);   // 6-12 meses = -2 a -0.5
        if (prazo <= 24) return interpolate(-0.5, 0.5, prazo, 12, 24);   // 12-24 meses = -0.5 a +0.5
        if (prazo <= 48) return interpolate(0.5, 1.5, prazo, 24, 48);    // 24-48 meses = +0.5 a +1.5
        if (prazo <= 72) return interpolate(1.5, 2.0, prazo, 48, 72);    // 48-72 meses = +1.5 a +2
        return 2.0;                                     // > 72 meses = +2
    }

    // Função de interpolação linear
    private double interpolate(double minVal, double maxVal, double current, double minRange, double maxRange) {
        if (maxRange <= minRange) return minVal;
        double ratio = (current - minRange) / (maxRange - minRange);
        return minVal + ratio * (maxVal - minVal);
    }

    private TipoPerfilRisco determinarPerfil(double score) {
        if (score <= 0.5) return TipoPerfilRisco.CONSERVADOR;    // -4 a +0.5
        if (score <= 4.0) return TipoPerfilRisco.MODERADO;       // +0.6 a +4.0
        return TipoPerfilRisco.AGRESSIVO;                         // +4.1+
    }
}