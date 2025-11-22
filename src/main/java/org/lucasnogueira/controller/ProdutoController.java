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
import org.lucasnogueira.model.dto.ProdutoRecomendadoResponse;
import org.lucasnogueira.service.ProdutoService;
import org.lucasnogueira.service.TelemetriaService;
import org.lucasnogueira.model.dto.SimulacaoResponseDTO;
import org.lucasnogueira.model.enums.TipoPerfilRisco;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Controller REST para operações de simulação de investimento
 */
@Path("/api/produtos-recomendados")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Produto", description = "Operações de recomendacoes de Produtos")
public class ProdutoController {

    @Inject
    ProdutoService produtoService;

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
                            ENDPOINT_KEY, "produtos-recomendados",
                            METHOD_KEY, "GET"
                    ));
                });
    }

    @GET
    @Path("/{perfil}")
    @Operation(
            summary = "Listar produtos recomendados por perfil de risco",
            description = "Retorna uma lista de produtos financeiros recomendados com base no perfil de risco informado. "
                    + "Perfis válidos: CONSERVADOR, MODERADO, AGRESSIVO."
    )
    @APIResponse(
            responseCode = "200",
            description = "Lista de produtos recomendados retornada com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            type = SchemaType.ARRAY,
                            implementation = ProdutoRecomendadoResponse.class
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos"
    )
    @APIResponse(
            responseCode = "404",
            description = "Nenhum produto recomendado encontrado para o perfil informado."
    )
    @APIResponse(
            responseCode = "500",
            description = "Erro interno do servidor"
    )
    public Response listarProdutosRecomendados(@PathParam("perfil") String perfil) {
        long startTime = System.nanoTime();
        totalRequests.incrementAndGet();

        String status = "500"; // Default para erro

        try {

            // Converta o perfil para enum, se necessário
            TipoPerfilRisco perfilRisco = TipoPerfilRisco.valueOf(perfil.toUpperCase());

            List<ProdutoRecomendadoResponse> produtos = produtoService.listarProdutosRecomendadosPorPerfil(perfilRisco);

            // Incrementar contador de sucesso
            successRequests.incrementAndGet();
            status = "200";

            long durationNanos = System.nanoTime() - startTime;
            double durationSeconds = durationNanos / 1_000_000_000.0;

            log.info("[REQUISICAO][PRODUTOS] - Finalizando requisicao em {}ms", Math.round(durationSeconds * 1000));

            return Response.ok(produtos).build();

        } catch (Exception e) {
            log.warn("[REQUISICAO][PRODUTOS] - Erro na requisicao: {}", e.getMessage());
            status = "500";
            throw e;

        } finally {
            // Calcular duração em segundos
            long endTime = System.nanoTime();
            double durationSeconds = (endTime - startTime) / 1_000_000_000.0;

            // Criar atributos para classificação das métricas
            Attributes attributes = Attributes.of(
                    ENDPOINT_KEY, "produtos-recomendados",
                    METHOD_KEY, "GET",
                    STATUS_KEY, status
            );

            // Registrar duração no OpenTelemetry
            httpServerDurationHistogram.record(durationSeconds, attributes);

            // Registrar contador de requisições no OpenTelemetry
            httpServerRequestsCounter.add(1, attributes);

            // Registrar métricas no TelemetriaService para coleta posterior
            telemetriaService.registrarRequisicao("produtos-recomendados", durationSeconds, Integer.parseInt(status));
        }
    }
}

