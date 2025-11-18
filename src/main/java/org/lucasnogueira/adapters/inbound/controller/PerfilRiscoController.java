package org.lucasnogueira.adapters.inbound.controller;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
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
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.lucasnogueira.application.service.PerfilRiscoServiceImpl;
import org.lucasnogueira.application.service.TelemetriaService;
import org.lucasnogueira.domain.simulacao.SimulacaoRequestDTO;
import org.lucasnogueira.domain.simulacao.SimulacaoResponseDTO;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Controller REST para operações de simulação de investimento
 */
@Path("/api/perfil-risco")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "PerfilRisco", description = "Operações de Perfil de Risco")
public class PerfilRiscoController {

    @Inject
    PerfilRiscoServiceImpl perfilRiscoServiceImpl;

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
        meter = openTelemetry.getMeter("investe-facil");

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
                            ENDPOINT_KEY, "/api/simulacao/processar",
                            METHOD_KEY, "POST"
                    ));
                });
    }

    @GET
    @Path("/{clienteId}")
    @Operation(
            summary = "Processar simulação de investimento",
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
            description = "Nenhum perfil risco para o cliente encontrado"
    )
    @APIResponse(
            responseCode = "500",
            description = "Erro interno do servidor"
    )
    public Response buscarPerfilRisco(@PathParam("clienteId") Long clienteId) {
        long startTime = System.nanoTime();
        totalRequests.incrementAndGet();

        String status = "500"; // Default para erro

        try {
//            log.info(" [REQUISICAO][SIMULACAO] - Iniciando requisicao de simulacao: {}", requestDTO);

            Object simulacao = perfilRiscoServiceImpl.buscarPerfilRiscoUseCase(clienteId);

            // Incrementar contador de sucesso
            successRequests.incrementAndGet();
            status = "200";

            long durationNanos = System.nanoTime() - startTime;
            double durationSeconds = durationNanos / 1_000_000_000.0;

            log.info(" [REQUISICAO][SIMULACAO] - Finalizando requisicao de simulacao em {}ms", Math.round(durationSeconds * 1000));

            return Response.status(201).entity(simulacao).build();

        /*} catch (APIEmprestimoAgoraException exception) {
            log.warn("[REQUISICAO][SIMULACAO] - Erro na requisicao: {}", exception.getMessage());
            status = "400";
            throw exception;*/

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
                    ENDPOINT_KEY, "/api/perfil-risco/",
                    METHOD_KEY, "POST",
                    STATUS_KEY, status
            );

            // Registrar duração no OpenTelemetry
            httpServerDurationHistogram.record(durationSeconds, attributes);

            // Registrar contador de requisições no OpenTelemetry
            httpServerRequestsCounter.add(1, attributes);

            // Registrar métricas no TelemetriaService para coleta posterior
            telemetriaService.registrarRequisicao("/api/simulacao/perfil-risco", durationSeconds, Integer.parseInt(status));
        }
    }
}
