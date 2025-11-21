package org.lucasnogueira.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.lucasnogueira.model.dto.SimulacaoRequestDTO;
import org.lucasnogueira.model.dto.ProdutoValidadoDTO;
import org.lucasnogueira.model.dto.ResultadoSimulacaoDTO;
import org.lucasnogueira.model.dto.HistoricoSimulacaoResponseDTO;
import org.lucasnogueira.model.dto.ListagemSimulacoesResponseDTO;
import org.lucasnogueira.model.dto.SimulacaoResponseDTO;
import org.lucasnogueira.model.dto.ValoresSimuladosPorProdutoDiaDTO;

import org.lucasnogueira.model.ProdutoComScore;
import org.lucasnogueira.model.entities.Produto;
import org.lucasnogueira.model.entities.Simulacao;
import org.lucasnogueira.model.enums.TipoPerfilRisco;
import org.lucasnogueira.repositories.SimulacaoRepository;
import org.lucasnogueira.util.SimulacaoFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@ApplicationScoped
public class SimulacaoService {

    @Inject
    ProdutoCacheService produtoCacheService;

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    PerfilRiscoCalculator perfilRiscoCalculator;

    @Inject
    ProdutoScoreCalculator scoreCalculator;

    @Inject
    SimulacaoFactory simulacaoFactory;

    @Transactional
    public SimulacaoResponseDTO simularInvestimento(SimulacaoRequestDTO requestDTO) {
        log.info("Iniciando simulação para cliente: {}", requestDTO.getClienteId());

        TipoPerfilRisco perfil = perfilRiscoCalculator.calcular(requestDTO);
        List<Produto> produtos = obterProdutosFiltrados(requestDTO);

        ProdutoComScore melhorProduto = encontrarMelhorProduto(produtos, requestDTO, perfil);

        SimulacaoResponseDTO response = criarResponseSimulacao(melhorProduto, requestDTO, perfil);
        persistirSimulacao(melhorProduto, requestDTO, perfil, response);

        log.debug("Simulação concluída com sucesso para cliente: {}", requestDTO.getClienteId());
        return response;
    }

    public ListagemSimulacoesResponseDTO buscarHistoricoSimulacoes(Integer pagina, Integer tamanhoPagina) {
        List<HistoricoSimulacaoResponseDTO> simulacoes = simulacaoRepository.listarSimulacoesPaginado(pagina, tamanhoPagina);
        long totalRegistros = simulacaoRepository.count();

        return ListagemSimulacoesResponseDTO.builder()
                .pagina(pagina)
                .qtdRegistros(totalRegistros)
                .qtdRegistrosPagina(simulacoes.size())
                .registros(simulacoes)
                .build();
    }

    public List<ValoresSimuladosPorProdutoDiaDTO> buscarValoresSimuladosPorProdutoEDia(LocalDate dataReferencia) {
        return simulacaoRepository.buscarValoresSimuladosPorProdutoEDia(dataReferencia);
    }

    public Object buscaSimulacoesPorCliente(Long clienteId) {
        return simulacaoRepository.buscaSimulacoesPorCliente(clienteId);
    }

    private List<Produto> obterProdutosFiltrados(SimulacaoRequestDTO requestDTO) {
        List<Produto> todosProdutos = produtoCacheService.findAllProdutos();

        if (requestDTO.getTipoProduto() == null || requestDTO.getTipoProduto().trim().isEmpty()) {
            return todosProdutos;
        }

        return aplicarFiltroPorTipo(todosProdutos, requestDTO.getTipoProduto().trim());
    }

    private List<Produto> aplicarFiltroPorTipo(List<Produto> produtos, String tipoSolicitado) {
        Optional<String> tipoEncontrado = produtoCacheService.findTiposDistintos()
                .stream()
                .filter(tipo -> correspondeAoTipo(tipo, tipoSolicitado))
                .findFirst();

        if (tipoEncontrado.isEmpty()) {
            log.warn("Tipo '{}' não encontrado. Usando todos os produtos.", tipoSolicitado);
            return produtos;
        }

        List<Produto> produtosFiltrados = produtos.stream()
                .filter(p -> p.getTipo().equalsIgnoreCase(tipoEncontrado.get()))
                .toList();

        if (produtosFiltrados.isEmpty()) {
            log.warn("Nenhum produto encontrado para o tipo: {}. Usando todos os produtos.", tipoEncontrado.get());
            return produtos;
        }

        log.debug("Filtrados {} produtos do tipo: {}", produtosFiltrados.size(), tipoEncontrado.get());
        return produtosFiltrados;
    }

    private boolean correspondeAoTipo(String tipoDisponivel, String tipoSolicitado) {
        String tipoDisponivelLower = tipoDisponivel.toLowerCase();
        String tipoSolicitadoLower = tipoSolicitado.toLowerCase();

        return tipoDisponivelLower.contains(tipoSolicitadoLower) ||
                tipoSolicitadoLower.contains(tipoDisponivelLower);
    }

    private ProdutoComScore encontrarMelhorProduto(List<Produto> produtos, SimulacaoRequestDTO requestDTO, TipoPerfilRisco perfil) {
        if (produtos.isEmpty()) {
            throw new IllegalStateException("Nenhum produto disponível para simulação");
        }

        return produtos.stream()
                .map(produto -> new ProdutoComScore(produto, scoreCalculator.calcular(produto, requestDTO, perfil, produtos)))
                .max(Comparator.comparingDouble(ProdutoComScore::getScore))
                .orElseThrow(() -> new IllegalStateException("Erro ao calcular melhor produto"));
    }

    private SimulacaoResponseDTO criarResponseSimulacao(ProdutoComScore melhorProduto, SimulacaoRequestDTO requestDTO, TipoPerfilRisco perfil) {
        Produto produto = melhorProduto.getProduto();

        SimulacaoResponseDTO response = new SimulacaoResponseDTO();
        response.setProdutoValidado(criarProdutoValidadoDTO(produto));
        response.setResultadoSimulacao(criarResultadoSimulacaoDTO(requestDTO, produto));
        response.setDataSimulacao(OffsetDateTime.now(ZoneOffset.UTC));

        return response;
    }

    private void persistirSimulacao(ProdutoComScore melhorProduto, SimulacaoRequestDTO requestDTO,
                                    TipoPerfilRisco perfil, SimulacaoResponseDTO response) {
        Simulacao simulacao = simulacaoFactory.criar(
                melhorProduto,
                requestDTO,
                perfil,
                response.getDataSimulacao(),
                response.getResultadoSimulacao().getValorFinal()
        );

        simulacaoRepository.persist(simulacao);
    }

    private ProdutoValidadoDTO criarProdutoValidadoDTO(Produto produto) {
        ProdutoValidadoDTO dto = new ProdutoValidadoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setTipo(produto.getTipo());
        dto.setRentabilidade(produto.getTaxaAnualOferecida().doubleValue());
        dto.setRisco(produto.getRisco());
        return dto;
    }

    private ResultadoSimulacaoDTO criarResultadoSimulacaoDTO(SimulacaoRequestDTO requestDTO, Produto produto) {
        ResultadoSimulacaoDTO dto = new ResultadoSimulacaoDTO();
        dto.setValorFinal(calcularValorFinal(requestDTO.getValor(), produto.getTaxaAnualOferecida(), requestDTO.getPrazoMeses()));
        dto.setRentabilidadeEfetiva(produto.getTaxaAnualOferecida().doubleValue());
        dto.setPrazoMeses(requestDTO.getPrazoMeses());
        return dto;
    }

    private BigDecimal calcularValorFinal(BigDecimal valorInicial, BigDecimal taxa, int meses) {
        BigDecimal anos = BigDecimal.valueOf(meses).divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal umMaisTaxa = BigDecimal.ONE.add(taxa);
        double potencia = Math.pow(umMaisTaxa.doubleValue(), anos.doubleValue());
        BigDecimal fator = BigDecimal.valueOf(potencia);
        return valorInicial.multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }
}