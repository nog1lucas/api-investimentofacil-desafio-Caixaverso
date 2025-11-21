package org.lucasnogueira.controller;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.lucasnogueira.exceptions.SmartInvestApiException;
import org.lucasnogueira.model.dto.*;
import org.lucasnogueira.service.SimulacaoService;
import org.lucasnogueira.service.TelemetriaService;

// OpenTelemetry imports
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributeKey;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Controller REST para operações de simulação de investimentos
 */
@Path("/api/simulacoes")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulação", description = "Operações de simulação de investimentos")
public class SimulacaoController {

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    OpenTelemetry openTelemetry;

    @Inject
    TelemetriaService telemetriaService;

    // OpenTelemetry métricas
    private Meter meter;
    private DoubleHistogram httpServerDurationHistogram;
    private LongCounter httpServerRequestsCounter;
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successRequests = new AtomicLong(0);

    // Attribute keys
    private static final AttributeKey<String> ENDPOINT_KEY = AttributeKey.stringKey("endpoint");
    private static final AttributeKey<String> METHOD_KEY = AttributeKey.stringKey("method");
    private static final AttributeKey<String> STATUS_KEY = AttributeKey.stringKey("status");

    @PostConstruct
    public void initMetrics() {
        // Criar meter
        meter = openTelemetry.getMeter("smartInvest");

        // Histogram para duração das requisições (em segundos)
        httpServerDurationHistogram = meter
                .histogramBuilder("http_server_duration_seconds")
                .setDescription("Tempo de resposta das requisições HTTP")
                .setUnit("s")
                .build();

        // Counter para requisições totais
        httpServerRequestsCounter = meter
                .counterBuilder("http_server_requests_total")
                .setDescription("Número total de requisições HTTP")
                .build();

        // Gauge para percentual de sucesso
        meter.gaugeBuilder("http_server_success_rate")
                .setDescription("Percentual de sucesso das requisições")
                .setUnit("%")
                .buildWithCallback(measurement -> {
                    long total = totalRequests.get();
                    long success = successRequests.get();
                    double successRate = total > 0 ? (double) success / total * 100.0 : 0.0;
                    measurement.record(successRate, Attributes.of(
                            ENDPOINT_KEY, "simular-investimento",
                            METHOD_KEY, "POST"
                    ));
                });
    }

    @POST
    @Path("/simular-investimento")
    @Operation(
            summary = "Processar simulação de investimentos",
            description = "Recebe solicitação de simulação, valida dados, calcula SCORE, persiste no banco de dados de forma síncrona e retorna resultados"
    )
    @APIResponse(
            responseCode = "201",
            description = "Simulação processada com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SimulacaoResponseDTO.class)
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos"
    )
    @APIResponse(
            responseCode = "404",
            description = "Nenhum produto disponivel para os parâmetros informados"
    )
    @APIResponse(
            responseCode = "500",
            description = "Erro interno do servidor"
    )
    public Response simular(@Valid SimulacaoRequestDTO requestDTO) throws Exception {
        long startTime = System.nanoTime();
        totalRequests.incrementAndGet();

        String status = "500"; // Default para erro

        try {
            log.info("[REQUISICAO][SIMULACAO] - Iniciando requisicao de simulacao: {}", requestDTO);

            SimulacaoResponseDTO simulacao = simulacaoService.simularInvestimento(requestDTO);

            // Incrementar contador de sucesso
            successRequests.incrementAndGet();
            status = "200";

            long durationNanos = System.nanoTime() - startTime;
            double durationSeconds = durationNanos / 1_000_000_000.0;

            log.info("[REQUISICAO][SIMULACAO] - Finalizando requisicao de simulacao em {}ms", Math.round(durationSeconds * 1000));

            return Response.status(201).entity(simulacao).build();

        } catch (SmartInvestApiException exception) {
            log.warn("[REQUISICAO][SIMULACAO] - Erro na requisicao: {}", exception.getMessage());
            status = "400";
            throw exception;

        } catch (Exception e) {
            log.warn("[REQUISICAO][SIMULACAO] - Erro na requisicao: {}", e.getMessage());
            status = "500";
            throw e;

        } finally {
            // Calcular duração em segundos
            long endTime = System.nanoTime();
            double durationSeconds = (endTime - startTime) / 1_000_000_000.0;

            // Criar atributos para classificação das métricas
            Attributes attributes = Attributes.of(
                    ENDPOINT_KEY, "simular-investimento",
                    METHOD_KEY, "POST",
                    STATUS_KEY, status
            );

            // Registrar duração no OpenTelemetry
            httpServerDurationHistogram.record(durationSeconds, attributes);

            // Registrar contador de requisições no OpenTelemetry
            httpServerRequestsCounter.add(1, attributes);

            // Registrar métricas no TelemetriaService para coleta posterior
            telemetriaService.registrarRequisicao("simular-investimento", durationSeconds, Integer.parseInt(status));
        }
    }

    /**
     * Endpoint para consultar o histórico completo de simulacaoes realizadas.
     */
    @GET
    @Operation(
            summary = "Listar histórico de simulações",
            description = "Retorna uma lista paginada contendo todas as simulações realizadas. "
                    + "Use os parâmetros de paginação para controlar o número de registros retornados por requisição."
    )
    @APIResponse(
            responseCode = "200",
            description = "Lista de simulações retornada com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ListagemSimulacoesResponseDTO.class)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Nenhuma simulação encontrada"
    )
    @APIResponse(
            responseCode = "500",
            description = "Erro interno do servidor"
    )
    public Response listarHistoricoDeSimulacoes(
            @Parameter(description = "Numero da pagina (começando em 0)", example = "0")
            @QueryParam("pagina") @DefaultValue("0") int pagina,

            @Parameter(description = "Tamanho da pagina", example = "20")
            @QueryParam("tamanhoPagina") @DefaultValue("20") int tamanhoPagina) {

        long startTime = System.nanoTime();
        String status = "500"; // Default para erro

        try {
            log.info("[REQUISICAO][LISTAGEM PAGINADA] - Iniciando listagem paginada de simulações");

            ListagemSimulacoesResponseDTO resultado = simulacaoService.buscarHistoricoSimulacoes(pagina, tamanhoPagina);

            log.info(String.format("Encontradas %d simulações na página %d",
                    resultado.getQtdRegistrosPagina(), pagina));

            long tempoExecucao = System.currentTimeMillis() - (startTime / 1_000_000);
            log.info("[REQUISICAO][LISTAGEM PAGINADA] - Finalizando a listagem paginada em {}ms\n", tempoExecucao);

            status = "200";
            successRequests.incrementAndGet();
            return Response.ok(resultado).build();

        } catch (Exception e) {
            log.error("Erro ao listar simulações: " + e.getMessage());
            status = "500";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "Erro interno do servidor"))
                    .build();
        } finally {
            // Calcular duração em segundos
            long endTime = System.nanoTime();
            double durationSeconds = (endTime - startTime) / 1_000_000_000.0;

            // Criar atributos para classificação das métricas
            Attributes attributes = Attributes.of(
                ENDPOINT_KEY, "simulacoes",
                METHOD_KEY, "GET",
                STATUS_KEY, status
            );

            // Registrar duração no OpenTelemetry
            httpServerDurationHistogram.record(durationSeconds, attributes);

            // Registrar contador de requisições no OpenTelemetry
            httpServerRequestsCounter.add(1, attributes);

            // Registrar métricas no TelemetriaService para coleta posterior
            telemetriaService.registrarRequisicao("simulacoes", durationSeconds, Integer.parseInt(status));
        }
    }

    /**
     * Endpoint para buscar histórico de simulacaoes
     */
    @GET
    @Path("/por-produto-dia")
    @Operation(
            summary = "Listar valores simulados por produto e dia",
            description = "Retorna uma lista paginada contendo os valores simulados, agrupados por produto e dia. "
                    + "Utilize os parâmetros de paginação para controlar a quantidade de registros retornados."
    )
    @APIResponse(
            responseCode = "200",
            description = "Simulação encontrada com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = SimulacaoResponseDTO.class)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Nenhum valor simulado encontrado para os parâmetros informados."
    )
    @APIResponse(
            responseCode = "500",
            description = "Erro interno do servidor"
    )
    public Response buscarValoresSimuladosPorProdutoDia(
            @Parameter(
                    description = "Data de referência no formato yyyy-MM-dd (obrigatório)",
                    example = "2024-01-15",
                    required = true
            )
            @QueryParam("dataReferencia") String dataReferenciaParam) {

        long startTime = System.nanoTime();
        String status = "500"; // Default para erro

        try {
            LocalDate dataReferencia;

            if (dataReferenciaParam == null || dataReferenciaParam.trim().isEmpty()) {
                dataReferencia = LocalDate.now();
            } else {
                // Tenta fazer o parse da data fornecida
                try {
                    dataReferencia = LocalDate.parse(dataReferenciaParam, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Formato de data inválido. Use o formato yyyy-MM-dd (ex: 2024-01-15)")
                            .build();
                }
            }

            log.info("[REQUISICAO][PRODUTO_DIA] - Consultando simulações para data: {}", dataReferencia);

            List<ValoresSimuladosPorProdutoDiaDTO> simulacao = simulacaoService.buscarValoresSimuladosPorProdutoEDia(dataReferencia);

            status = "200";

            log.info("[REQUISICAO][PRODUTO_DIA] - Encontradas {} simulações agrupadas por produto para a data: {}",
                    simulacao.size(), dataReferencia);

            return Response.ok(simulacao).build();

        } catch (Exception e) {
            log.error("[REQUISICAO][PRODUTO_DIA] - Erro ao buscar simulações: {}", e.getMessage());
            status = "500";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("INTERNAL_ERROR", "Erro interno do servidor"))
                    .build();
        } finally {
            // Calcular duração em segundos
            long endTime = System.nanoTime();
            double durationSeconds = (endTime - startTime) / 1_000_000_000.0;

            // Criar atributos para classificação das métricas
            Attributes attributes = Attributes.of(
                    ENDPOINT_KEY, "por-produto-dia",
                    METHOD_KEY, "GET",
                    STATUS_KEY, status
            );

            // Registrar duração no OpenTelemetry
            httpServerDurationHistogram.record(durationSeconds, attributes);

            // Registrar contador de requisições no OpenTelemetry
            httpServerRequestsCounter.add(1, attributes);

            // Registrar métricas no TelemetriaService para coleta posterior
            telemetriaService.registrarRequisicao("por-produto-dia", durationSeconds, Integer.parseInt(status));
        }
    }

    /**
     * Endpoint para verificar saúde do serviço
     */
    @GET
    @Path("/health")
    @Operation(
            summary = "Verificar saúde do serviço de simulação",
            description = "Endpoint para verificar se o serviço de simulação está funcionando"
    )
    @APIResponse(
            responseCode = "200",
            description = "Serviço funcionando corretamente",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = HealthResponseDTO.class)
            )
    )
    @APIResponse(
            responseCode = "503",
            description = "Serviço indisponível"
    )
    public Response health() {
        return Response.ok(HealthResponseDTO.builder()
                .status("OK")
                .mensagem("Serviço de simulação funcionando")
                .build()).build();
    }

    /**
     * Classe para resposta de erro
     */
    public static class ErrorResponse {
        public String codigo;
        public String mensagem;

        public ErrorResponse() {}

        public ErrorResponse(String codigo, String mensagem) {
            this.codigo = codigo;
            this.mensagem = mensagem;
        }
    }

}