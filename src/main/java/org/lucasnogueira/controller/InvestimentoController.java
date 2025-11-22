package org.lucasnogueira.controller;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.lucasnogueira.model.dto.InvestimentoClienteResponseDTO;
import org.lucasnogueira.service.SimulacaoService;
import org.lucasnogueira.service.TelemetriaService;
import org.lucasnogueira.model.dto.SimulacaoResponseDTO;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Controller REST para operações de simulação de investimento
 */
@Path("/api/investimentos")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Investimento", description = "Operações de histórico de investimento")
public class InvestimentoController {

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
                            ENDPOINT_KEY, "investimentos",
                            METHOD_KEY, "GET"
                    ));
                });
    }

    @GET
    @Path("/{clienteId}")
    @Operation(
            summary = "Consultar histórico de investimentos do cliente",

            description = "Busca todas as simulações de investimento realizadas pelo cliente identificado pelo parâmetro `clienteId`. "
    )
    @APIResponse(
            responseCode = "200",
            description = "Lista de investimentos retornada com sucesso.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            type = SchemaType.ARRAY,
                            implementation = InvestimentoClienteResponseDTO.class
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos"
    )
    @APIResponse(
            responseCode = "404",
            description = "Nenhum histórico de investimento encontrado para o cliente informado"
    )
    @APIResponse(
            responseCode = "500",
            description = "Erro interno do servidor"
    )
    public Response listarHistoricoInvestimentos(@PathParam("clienteId") Long clienteId) {
        long startTime = System.nanoTime();
        totalRequests.incrementAndGet();

        String status = "500"; // Default para erro

        try {

            List<InvestimentoClienteResponseDTO> resultado = simulacaoService.buscaSimulacoesPorCliente(clienteId);

            // Incrementar contador de sucesso
            successRequests.incrementAndGet();
            status = "200";

            long durationNanos = System.nanoTime() - startTime;
            double durationSeconds = durationNanos / 1_000_000_000.0;

            log.info(" [REQUISICAO][INVESTIMENTOS] - Finalizando requisicao em {}ms", Math.round(durationSeconds * 1000));

            return Response.status(200).entity(resultado).build();

        } catch (Exception e) {
            log.warn("[REQUISICAO][INVESTIMENTOS] - Erro na requisicao: {}", e.getMessage());
            status = "500";
            throw e;

        } finally {
            // Calcular duração em segundos
            long endTime = System.nanoTime();
            double durationSeconds = (endTime - startTime) / 1_000_000_000.0;

            // Criar atributos para classificação das métricas
            Attributes attributes = Attributes.of(
                    ENDPOINT_KEY, "investimentos",
                    METHOD_KEY, "GET",
                    STATUS_KEY, status
            );

            // Registrar duração no OpenTelemetry
            httpServerDurationHistogram.record(durationSeconds, attributes);

            // Registrar contador de requisições no OpenTelemetry
            httpServerRequestsCounter.add(1, attributes);

            // Registrar métricas no TelemetriaService para coleta posterior
            telemetriaService.registrarRequisicao("investimentos", durationSeconds, Integer.parseInt(status));
        }
    }
}

