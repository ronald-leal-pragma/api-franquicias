package com.nequi.franchise.infrastructure.entrypoints.reactiveweb;

import com.nequi.franchise.infrastructure.driven_adapters.mongo_repository.ResilienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Handler para endpoints de monitoreo del Circuit Breaker y métricas de resiliencia.
 */
@Component
@RequiredArgsConstructor
@Tag(name = "Monitoreo", description = "Endpoints para monitorear el estado del sistema y circuit breaker")
public class ResilienceHandler {

    private final ResilienceService resilienceService;

    @Operation(
            summary = "Obtener estado del Circuit Breaker",
            description = "Retorna el estado actual del Circuit Breaker de MongoDB (CLOSED, OPEN, HALF_OPEN)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Estado del Circuit Breaker obtenido exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CircuitBreakerStateResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"state\": \"CLOSED\", \"description\": \"Circuit Breaker está cerrado, las peticiones se procesan normalmente\"}"
                                    )
                            )
                    )
            }
    )
    public Mono<ServerResponse> getCircuitBreakerState(ServerRequest request) {
        String state = resilienceService.getCircuitBreakerState();
        String description = getStateDescription(state);

        CircuitBreakerStateResponse response = new CircuitBreakerStateResponse(state, description);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    @Operation(
            summary = "Obtener métricas del Circuit Breaker",
            description = "Retorna métricas detalladas del Circuit Breaker incluyendo tasas de fallo, llamadas exitosas/fallidas, etc.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Métricas obtenidas exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResilienceService.CircuitBreakerMetrics.class)
                            )
                    )
            }
    )
    public Mono<ServerResponse> getCircuitBreakerMetrics(ServerRequest request) {
        ResilienceService.CircuitBreakerMetrics metrics = resilienceService.getCircuitBreakerMetrics();

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(metrics);
    }

    private String getStateDescription(String state) {
        return switch (state) {
            case "CLOSED" -> "Circuit Breaker está cerrado, las peticiones se procesan normalmente";
            case "OPEN" -> "Circuit Breaker está abierto, las peticiones a MongoDB están siendo rechazadas por alto índice de fallos";
            case "HALF_OPEN" -> "Circuit Breaker está semi-abierto, probando si el servicio se ha recuperado";
            case "DISABLED" -> "Circuit Breaker está deshabilitado";
            case "FORCED_OPEN" -> "Circuit Breaker forzado a estado abierto";
            default -> "Estado desconocido: " + state;
        };
    }

    /**
     * DTO para respuesta del estado del Circuit Breaker.
     */
    @Schema(description = "Estado del Circuit Breaker")
    public record CircuitBreakerStateResponse(
            @Schema(description = "Estado actual", example = "CLOSED",
                    allowableValues = {"CLOSED", "OPEN", "HALF_OPEN", "DISABLED", "FORCED_OPEN"})
            String state,

            @Schema(description = "Descripción del estado",
                    example = "Circuit Breaker está cerrado, las peticiones se procesan normalmente")
            String description
    ) {}
}

