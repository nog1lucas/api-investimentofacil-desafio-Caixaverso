package org.lucasnogueira.adapters.inbound.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.lucasnogueira.adapters.inbound.dto.HealthResponseDTO;
import org.lucasnogueira.application.service.TelemetriaService;
import org.lucasnogueira.domain.telemetria.TelemetriaResponseDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Controller REST para operações de telemetria
 */
@Path("/api/telemetria")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Telemetria", description = "Operações de telemetria e métricas da aplicação")
public class TelemetriaController {

    @Inject
    TelemetriaService telemetriaService;

    @GET
    @Operation(
            summary = "Coletar métricas de telemetria",
            description = "Retorna as métricas de telemetria para uma data específica ou data atual se não informada"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Métricas coletadas com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TelemetriaResponseDTO.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Formato de data inválido. Use o formato yyyy-MM-dd"
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor"
            )
    })
    public Response getTelemetria(
            @Parameter(
                    description = "Data de inicio no formato yyyy-MM-dd (opcional, padrão: data atual)",
                    example = "2024-01-15",
                    required = false
            )
            @QueryParam("dataInicio") String dataInicioParam,
            @Parameter(
                    description = "Data Fim no formato yyyy-MM-dd (opcional, padrão: data atual)",
                    example = "2024-01-15",
                    required = false
            )
            @QueryParam("dataFim") String dataFimParam) {
        try {
            LocalDate dataInicio = parseData(dataInicioParam, LocalDate.now(), "dataInicio");
            LocalDate dataFim = parseData(dataFimParam, null, "dataFim");

            TelemetriaResponseDTO telemetria = telemetriaService.coletarMetricas(dataInicio, dataFim);
            return Response.ok(telemetria).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Erro ao coletar telemetria: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Endpoint para verificar saúde do serviço de telemetria
     */
    @GET
    @Path("/health")
    @Operation(
            summary = "Verificar saúde do serviço de telemetria",
            description = "Endpoint para verificar se o serviço de telemetria está funcionando"
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Serviço funcionando corretamente"
            ),
            @APIResponse(
                    responseCode = "503",
                    description = "Serviço indisponível"
            )
    })
    public Response health() {
        try {
            // Testa conectividade com banco de dados fazendo uma consulta simples
            telemetriaService.coletarMetricas(LocalDate.now(), LocalDate.now());

            return Response.ok(HealthResponseDTO.builder()
                    .status("OK")
                    .mensagem("Serviço de telemetria funcionando")
                    .build()).build();

        } catch (Exception e) {
            log.error("Erro no health check de telemetria: " + e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(HealthResponseDTO.builder()
                            .status("ERROR")
                            .mensagem("Serviço de telemetria indisponível: " + e.getMessage())
                            .build())
                    .build();
        }
    }

    private LocalDate parseData(String dataParam, LocalDate defaultValue, String nomeCampo) {
        if (dataParam == null || dataParam.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return LocalDate.parse(dataParam, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Formato de data inválido para " + nomeCampo + ". Use o formato yyyy-MM-dd (ex: 2024-01-15)"
            );
        }
    }
}
