package org.lucasnogueira.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.lucasnogueira.application.usecases.SimulacaoUseCases;
import org.lucasnogueira.domain.produto.Produto;
import org.lucasnogueira.domain.produto.ProdutoRepository;
import org.lucasnogueira.domain.simulacao.*;
import org.lucasnogueira.enums.TipoPerfilRisco;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Serviço responsável pela lógica de negócio das simulações de investimentos
 */
@Slf4j
@ApplicationScoped
public class SimulacaoServiceImpl implements SimulacaoUseCases {

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    SimulacaoRepository simulacaoRepository;

    private static final Map<String, Double> DEFAULT_BY_RATING = Map.of(
            "AAA", 0.0001, "AA", 0.0005, "A", 0.001, "BBB", 0.005,
            "BB", 0.02, "B", 0.05, "CCC", 0.15, "D", 1.0, "NR", 0.03
    );

    @Transactional
    public SimulacaoResponseDTO simularInvestimento(SimulacaoRequestDTO requestDTO) {
        int pontuacao = calcularPontuacao(requestDTO);
        TipoPerfilRisco perfil = calcularPerfil(requestDTO);

        List<Produto> produtos = produtoRepository.findAll();

        double retornoMaximo = produtos.stream()
                .mapToDouble(p -> p.getTaxaAnualOferecida().doubleValue())
                .max().orElse(0.01);

        double liquidezMinima = produtos.stream()
                .mapToDouble(Produto::getLiquidezDias)
                .min().orElse(0.0);

        double liquidezMaxima = produtos.stream()
                .mapToDouble(Produto::getLiquidezDias)
                .max().orElse(1.0);

        Optional<Produto> melhorProduto = produtos.stream()
                .max(Comparator.comparingDouble(p -> calcularScore(
                        p, requestDTO, perfil, retornoMaximo, liquidezMinima, liquidezMaxima)));

        SimulacaoResponseDTO response = new SimulacaoResponseDTO();

        melhorProduto.ifPresent(produto -> {
            ProdutoValidadoDTO produtoValidado = montarProdutoValidadoDTO(produto, perfil);
            ResultadoSimulacaoDTO resultadoSimulacao = montarResultadoSimulacaoDTO(requestDTO, produto);

            response.setProdutoValidado(produtoValidado);
            response.setResultadoSimulacao(resultadoSimulacao);
            LocalDateTime dataSimulacao = LocalDateTime.now();
            response.setDataSimulacao(dataSimulacao);

            Simulacao simulacao = new Simulacao();
            simulacao.setCodigoCliente(requestDTO.getClienteId());
            simulacao.setCodigoProduto(produto.getId());
            simulacao.setPrazoMeses(requestDTO.getPrazoMeses());
            simulacao.setValorFinal(resultadoSimulacao.getValorFinal());
            simulacao.setValorInvestido(requestDTO.getValor());
            simulacao.setDataSimulacao(dataSimulacao);
            simulacao.setPontuacao(pontuacao);
            simulacao.setPerfilRisco(perfil.name());

            simulacaoRepository.persist(simulacao);
        });

        return response;
    }

    @Override
    public ListagemSimulacoesResponseDTO buscarHistoricoSimulacoes(Integer pagina, Integer tamanhoPagina) {
        List<Simulacao> simulacoes = simulacaoRepository.findAllPaged(pagina, tamanhoPagina);
        long totalRegistros = simulacaoRepository.countAll();

        return ListagemSimulacoesResponseDTO.builder()
                .pagina(pagina)
                .qtdRegistros(Math.toIntExact(totalRegistros))
                .qtdRegistrosPagina(simulacoes.size())
                .registros(simulacoes)
                .build();
    }

    @Override
    public List<ValoresSimuladosPorProdutoDiaDTO> buscarValoresSimuladosPorProdutoEDia(Integer pagina, Integer tamanhoPagina) {
        return simulacaoRepository.buscarValoresSimuladosPorProdutoEDia();
    }

    private ProdutoValidadoDTO montarProdutoValidadoDTO(Produto produto, TipoPerfilRisco perfil) {
        ProdutoValidadoDTO dto = new ProdutoValidadoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setTipo(produto.getTipo());
        dto.setRentabilidade(produto.getTaxaAnualOferecida().doubleValue());
        dto.setRisco(classificarRisco(produto.getRating(), perfil));
        return dto;
    }

    private ResultadoSimulacaoDTO montarResultadoSimulacaoDTO(SimulacaoRequestDTO requestDTO, Produto produto) {
        ResultadoSimulacaoDTO dto = new ResultadoSimulacaoDTO();
        dto.setValorFinal(calculaValorFinal(requestDTO.getValor(), produto.getTaxaAnualOferecida(), requestDTO.getPrazoMeses()));
        dto.setRentabilidadeEfetiva(produto.getTaxaAnualOferecida().doubleValue());
        dto.setPrazoMeses(requestDTO.getPrazoMeses());
        return dto;
    }

    private String classificarRisco(String rating, TipoPerfilRisco perfil) {
        double risco = normalizarRisco(rating, perfil);
        if (risco >= 0.7) return "Baixo";
        if (risco >= 0.4) return "Médio";
        return "Alto";
    }

    private int calcularPontuacao(SimulacaoRequestDTO requestDTO) {
        int score = 0;
        BigDecimal valor = requestDTO.getValor();
        int prazo = requestDTO.getPrazoMeses();

        if (valor.compareTo(new BigDecimal("100000")) > 0) score += 2;
        else if (valor.compareTo(new BigDecimal("20000")) > 0) score += 1;
        else score -= 1;

        if (prazo <= 12) score -= 2;
        else if (prazo > 36) score += 1;

        return score + 3;
    }

    private TipoPerfilRisco calcularPerfil(SimulacaoRequestDTO requestDTO) {
        int score = 0;
        BigDecimal valor = requestDTO.getValor();
        int prazo = requestDTO.getPrazoMeses();

        if (valor.compareTo(new BigDecimal("100000")) > 0) score += 2;
        else if (valor.compareTo(new BigDecimal("20000")) > 0) score += 1;
        else score -= 1;

        if (prazo <= 12) score -= 2;
        else if (prazo > 36) score += 1;

        if (score <= 0) return TipoPerfilRisco.CONSERVADOR;
        if (score <= 2) return TipoPerfilRisco.MODERADO;
        return TipoPerfilRisco.AGRESSIVO;
    }

    private double calcularScore(Produto produto, SimulacaoRequestDTO req, TipoPerfilRisco perfil,
                                 double retornoMaximo, double liquidezMin, double liquidezMax) {
        double wRetorno, wLiquidez, wRisco;
        switch (perfil) {
            case CONSERVADOR -> { wRetorno = 0.15; wLiquidez = 0.45; wRisco = 0.25; }
            case MODERADO -> { wRetorno = 0.30; wLiquidez = 0.25; wRisco = 0.25; }
            default -> { wRetorno = 0.40; wLiquidez = 0.10; wRisco = 0.20; }
        }

        double taxaBruta = produto.getTaxaAnualOferecida().doubleValue();
        double aliquotaIr = calcularAliquotaIr(req.getPrazoMeses() * 30, produto.getTipo());
        double taxaLiquida = taxaBruta * (1 - aliquotaIr);

        double retornoNorm = taxaLiquida / retornoMaximo;
        double liquidezNorm = normalizarLiquidez(produto.getLiquidezDias(), req.getPrazoMeses() * 30, liquidezMin, liquidezMax);
        double riscoNorm = normalizarRisco(produto.getRating(), perfil);

        double bonusTipo = produto.getTipo().equalsIgnoreCase(req.getTipoProduto()) ? 0.1 : 0.0;

        double score = wRetorno * retornoNorm + wLiquidez * liquidezNorm + wRisco * riscoNorm + bonusTipo;
        return Math.max(0.0, Math.min(1.0, score));
    }

    private double normalizarLiquidez(double diasLiquidez, int prazoClienteDias, double min, double max) {
        if (diasLiquidez <= prazoClienteDias) return 1.0;
        if (max <= min) return 0.0;
        return Math.max(0, 1.0 - (diasLiquidez - prazoClienteDias) / (max - min));
    }

    private double normalizarRisco(String rating, TipoPerfilRisco perfil) {
        double nivelRisco = mapRatingToRiskLevel(rating);
        return switch (perfil) {
            case CONSERVADOR -> 1.0 - (nivelRisco - 1.0) / 9.0;
            case MODERADO -> 1.0 - Math.abs(5.0 - nivelRisco) / 5.0;
            default -> (nivelRisco - 1.0) / 9.0;
        };
    }

    private double mapRatingToRiskLevel(String rating) {
        return switch (rating) {
            case "AAA" -> 1.0;
            case "AA" -> 2.0;
            case "A" -> 3.0;
            case "BBB" -> 5.0;
            case "BB" -> 6.5;
            case "B" -> 8.0;
            case "CCC" -> 9.5;
            case "D" -> 10.0;
            default -> 6.0;
        };
    }

    private double calcularAliquotaIr(int dias, String tipo) {
        if (tipo.equalsIgnoreCase("LCI") || tipo.equalsIgnoreCase("LCA")) return 0.0;
        if (tipo.equalsIgnoreCase("FII")) return 0.0;
        if (tipo.equalsIgnoreCase("ACAO")) return 0.15;
        if (dias <= 180) return 0.225;
        if (dias <= 360) return 0.20;
        if (dias <= 720) return 0.175;
        return 0.15;
    }

    private BigDecimal calculaValorFinal(BigDecimal valorInicial, BigDecimal taxa, int meses) {
        // Juros compostos: VF = VI * (1 + taxa) ^ anos
        BigDecimal anos = BigDecimal.valueOf(meses).divide(BigDecimal.valueOf(12), 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal umMaisTaxa = BigDecimal.ONE.add(taxa);
        double potencia = Math.pow(umMaisTaxa.doubleValue(), anos.doubleValue());
        BigDecimal fator = BigDecimal.valueOf(potencia);
        return valorInicial.multiply(fator).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}