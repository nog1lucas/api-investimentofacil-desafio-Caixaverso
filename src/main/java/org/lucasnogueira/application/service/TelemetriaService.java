package org.lucasnogueira.application.service;

import io.smallrye.context.api.ManagedExecutorConfig;
import io.smallrye.context.api.NamedInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.lucasnogueira.domain.telemetria.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Serviço responsável por coletar e processar métricas do OpenTelemetry
 * com persistência no banco de dados SQLServer local
 */
@Slf4j
@ApplicationScoped
public class TelemetriaService {

    @Inject
    OpenTelemetry openTelemetry;

    @Inject
    TelemetriaRepository telemetriaRepository;

    @Inject
    @ManagedExecutorConfig(propagated = ThreadContext.ALL_REMAINING)
    @NamedInstance("MyExecutor")
    ManagedExecutor managedExecutor;

    @ConfigProperty(name = "metricas.flush.limite.requisicoes", defaultValue = "10")
    int limiteRequisicoesSimulacao;

    // Cache em memória para métricas do dia atual (performance)
    private final Map<String, EndpointMetrics> endpointMetricsCache = new ConcurrentHashMap<>();
    private LocalDate cacheDate = LocalDate.now();

    // Attribute keys para buscar métricas
    private static final AttributeKey<String> ENDPOINT_KEY = AttributeKey.stringKey("endpoint");
    private static final AttributeKey<String> METHOD_KEY = AttributeKey.stringKey("method");
    private static final AttributeKey<String> STATUS_KEY = AttributeKey.stringKey("status");

    /**
     * Classe interna para armazenar métricas em cache
     */
    private static class EndpointMetrics {
        private final AtomicLong totalRequests = new AtomicLong(0);
        private final AtomicLong successRequests = new AtomicLong(0);
        private final AtomicLong errorRequests = new AtomicLong(0);
        private final AtomicReference<Double> totalDuration = new AtomicReference<>(0.0);
    }

    /**
     * Registra uma requisição com duração e status code
     */
    public void registrarRequisicao(String endpoint, double durationSeconds, int statusCode) {
        // Converte para milissegundos
        double durationMs = durationSeconds * 1000;

        // Atualiza cache em memória
        atualizarCache(endpoint, durationMs, statusCode >= 200 && statusCode < 400);

        // Se for endpoint de simulação, persiste imediatamente de forma assíncrona
        if (endpoint.contains("/simulacao")) {
            log.debug("Persistindo métricas de forma assíncrona após requisição de simulação: {}", endpoint);
            persistirCacheNoBancoAsync();
        }
    }

    /**
     * Registra uma requisição apenas com duração (assume sucesso)
     */
    public void registrarRequisicao(String endpoint, double durationSeconds) {
        registrarRequisicao(endpoint, durationSeconds, 200);
    }

    /**
     * Atualiza o cache em memória
     */
    private void atualizarCache(String endpoint, double durationMs, boolean isSuccess) {
        // Verifica se mudou o dia (limpa cache se necessário)
        LocalDate hoje = LocalDate.now();
        if (!hoje.equals(cacheDate)) {
            log.info("Mudança de dia detectada. Persistindo cache de forma assíncrona e limpando para nova data: {}", hoje);
            persistirCacheNoBancoAsync();
            endpointMetricsCache.clear();
            cacheDate = hoje;
        }

        EndpointMetrics metrics = endpointMetricsCache.computeIfAbsent(endpoint, k -> new EndpointMetrics());

        metrics.totalRequests.incrementAndGet();
        if (isSuccess) {
            metrics.successRequests.incrementAndGet();
        } else {
            metrics.errorRequests.incrementAndGet();
        }

        // Atualiza durações
        metrics.totalDuration.updateAndGet(current -> current + durationMs);
    }

    /**
     * Persiste o cache atual no banco de dados
     */
    /**
     * Persiste o cache no banco de dados de forma assíncrona
     */
    private void persistirCacheNoBancoAsync() {
        if (endpointMetricsCache.isEmpty()) {
            return;
        }

        // Cria uma cópia do cache para processamento assíncrono
        Map<String, EndpointMetrics> cacheSnapshot = new ConcurrentHashMap<>(endpointMetricsCache);
        LocalDate dataSnapshot = cacheDate;

        managedExecutor.runAsync(() -> {
            long inicio = System.currentTimeMillis();

            try {
                for (Map.Entry<String, EndpointMetrics> entry : cacheSnapshot.entrySet()) {
                    String endpoint = entry.getKey();
                    EndpointMetrics metrics = entry.getValue();

                    if (metrics.totalRequests.get() > 0) {
                        salvarOuAtualizarMetrica(endpoint, dataSnapshot, metrics);
                    }
                }

                long tempoExecucao = System.currentTimeMillis() - inicio;
                log.info("[METRICAS][PERSISTENCIA] - Cache de métricas persistido no banco de forma assíncrona em {}ms para a data: {}", tempoExecucao, dataSnapshot);

            } catch (Exception e) {
                log.error("[ERRO][METRICAS] - Erro ao persistir cache de métricas no banco de forma assíncrona", e);
//                throw new APIEmprestimoAgoraException("[ERRO][METRICAS] - Erro ao persistir cache de métricas no banco", e);
            }
        }).exceptionally(ex -> {
            log.error("[ERRO][METRICAS] - Exceção assíncrona não tratada na persistência de métricas", ex);
            return null;
        });

        // Limpa o cache após iniciar a persistência assíncrona para evitar duplicação
        endpointMetricsCache.clear();
    }

    /**
     * Persiste o cache no banco de dados de forma síncrona (mantido para compatibilidade)
     */
    private void persistirCacheNoBanco() {
        if (endpointMetricsCache.isEmpty()) {
            return;
        }

        try {
            for (Map.Entry<String, EndpointMetrics> entry : endpointMetricsCache.entrySet()) {
                String endpoint = entry.getKey();
                EndpointMetrics metrics = entry.getValue();

                if (metrics.totalRequests.get() > 0) {
                    salvarOuAtualizarMetrica(endpoint, cacheDate, metrics);
                }
            }

            // Limpa o cache após persistir para evitar duplicação
            endpointMetricsCache.clear();
            log.info("Cache de métricas persistido no banco e limpo para a data: {}", cacheDate);
        } catch (Exception e) {
            log.error("Erro ao persistir cache de métricas no banco", e);
        }
    }

    /**
     * Salva ou atualiza uma métrica no banco de dados
     */
    private void salvarOuAtualizarMetrica(String endpoint, LocalDate data, EndpointMetrics metrics) {
        Optional<Telemetria> metricaExistente = Optional.ofNullable(telemetriaRepository.buscarPorEndpointEData(endpoint, data));

        Telemetria telemetria;
        if (metricaExistente.isPresent()) {
            // Atualiza métrica existente
            telemetria = metricaExistente.get();
            telemetria.setQuantidadeChamadas(telemetria.getQuantidadeChamadas() + metrics.totalRequests.get());
            telemetria.setQuantidadeChamadasSucesso(telemetria.getQuantidadeChamadasSucesso() + metrics.successRequests.get());
            telemetria.setQuantidadeChamadasErro(telemetria.getQuantidadeChamadasErro() + metrics.errorRequests.get());

            BigDecimal novaDuracaoTotal = telemetria.getTempoRespostaMs().add(BigDecimal.valueOf(metrics.totalDuration.get()));
            telemetria.setTempoRespostaMs(novaDuracaoTotal);

        } else {
            // Cria nova métrica
            telemetria = Telemetria.builder()
                    .nomeServico(endpoint)
                    .dataReferencia(data)
                    .quantidadeChamadas(metrics.totalRequests.get())
                    .quantidadeChamadasSucesso(metrics.successRequests.get())
                    .quantidadeChamadasErro(metrics.errorRequests.get())
                    .tempoRespostaMs(BigDecimal.valueOf(metrics.totalDuration.get()))
                    .build();
        }

        telemetriaRepository.persist(telemetria);
    }


    /**
     * Coleta métricas para uma data específica
     */
    public TelemetriaResponseDTO coletarMetricas(LocalDate dataInicio, LocalDate dataFim) {
        List<TelemetriaServicoDTO> endpoints = new ArrayList<>();

        // Busca métricas do banco de dados
        List<Telemetria> telemetrias = telemetriaRepository.buscarPorPeriodo(dataInicio, dataFim);

        System.out.println(telemetrias);

        for (Telemetria telemetria : telemetrias) {
            if (telemetria.getQuantidadeChamadas() > 0) {
                TelemetriaServicoDTO endpointDTO = criarTelemetriaServicoDTO(telemetria);
                endpoints.add(endpointDTO);
            }
        }

        TelemetriaResponseDTO response = new TelemetriaResponseDTO();
        response.setPeriodo(PeriodoDTO.from(dataInicio, dataFim));
        response.setServicos(endpoints);

        return response;
    }

    /**
     * Coleta métricas para a data atual (compatibilidade)
     */
    public TelemetriaResponseDTO coletarMetricas() {
        return coletarMetricas(LocalDate.now(), LocalDate.now());
    }

    /**
     * Cria TelemetriaEndpointDTO a partir de MetricaEndpoint
     */
    private TelemetriaServicoDTO criarTelemetriaServicoDTO(Telemetria telemetria) {
        BigDecimal tempoMedio = telemetria.calcularTempoMedio();
        BigDecimal percentualSucesso = telemetria.calcularPercentualSucesso();

        return new TelemetriaServicoDTO(
                telemetria.getNomeServico(),
                telemetria.getQuantidadeChamadas().intValue(),
                tempoMedio.intValue(),
                percentualSucesso.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP)
        );
    }

}