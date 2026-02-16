package com.nequi.franchise.infrastructure.driven_adapters.mongo_repository;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Componente que aplica patrones de resiliencia (Circuit Breaker, Retry, Time Limiter)
 * a las operaciones reactivas de MongoDB.
 *
 * <p>Protege el servicio contra:
 * - Fallos temporales de MongoDB
 * - Timeouts prolongados
 * - Sobrecarga del servidor de base de datos
 * - Conexiones perdidas
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResilienceService {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final TimeLimiterRegistry timeLimiterRegistry;

    private static final String MONGODB_INSTANCE = "mongodb";

    /**
     * Aplica Circuit Breaker, Retry y Time Limiter a una operación Mono de MongoDB.
     *
     * @param operation Operación reactiva a proteger
     * @param operationName Nombre de la operación para logging
     * @param <T> Tipo de retorno
     * @return Mono protegido con patrones de resiliencia
     */
    public <T> Mono<T> executeWithResilience(Mono<T> operation, String operationName) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(MONGODB_INSTANCE);
        Retry retry = retryRegistry.retry(MONGODB_INSTANCE);
        TimeLimiter timeLimiter = timeLimiterRegistry.timeLimiter(MONGODB_INSTANCE);

        logCircuitBreakerState(circuitBreaker, operationName);

        return operation
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .doOnError(error -> log.error("Error en operación '{}': {}", operationName, error.getMessage()))
                .doOnSuccess(result -> log.debug("Operación '{}' completada exitosamente", operationName));
    }

    /**
     * Aplica Circuit Breaker, Retry y Time Limiter a una operación Flux de MongoDB.
     *
     * @param operation Operación reactiva flux a proteger
     * @param operationName Nombre de la operación para logging
     * @param <T> Tipo de elementos del flux
     * @return Flux protegido con patrones de resiliencia
     */
    public <T> Flux<T> executeFluxWithResilience(Flux<T> operation, String operationName) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(MONGODB_INSTANCE);
        Retry retry = retryRegistry.retry(MONGODB_INSTANCE);
        TimeLimiter timeLimiter = timeLimiterRegistry.timeLimiter(MONGODB_INSTANCE);

        logCircuitBreakerState(circuitBreaker, operationName);

        return operation
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(TimeLimiterOperator.of(timeLimiter))
                .doOnError(error -> log.error("Error en operación Flux '{}': {}", operationName, error.getMessage()))
                .doOnComplete(() -> log.debug("Operación Flux '{}' completada", operationName));
    }

    /**
     * Ejecuta una operación con solo Circuit Breaker (sin Retry ni Time Limiter).
     * Útil para operaciones que no deben reintentarse.
     *
     * @param operation Operación a ejecutar
     * @param operationName Nombre para logging
     * @param <T> Tipo de retorno
     * @return Mono protegido solo con Circuit Breaker
     */
    public <T> Mono<T> executeWithCircuitBreakerOnly(Mono<T> operation, String operationName) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(MONGODB_INSTANCE);

        logCircuitBreakerState(circuitBreaker, operationName);

        return operation
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .doOnError(error -> log.error("Error en operación '{}': {}", operationName, error.getMessage()));
    }

    /**
     * Obtiene el estado actual del Circuit Breaker de MongoDB.
     *
     * @return Estado del circuit breaker (CLOSED, OPEN, HALF_OPEN)
     */
    public String getCircuitBreakerState() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(MONGODB_INSTANCE);
        return circuitBreaker.getState().name();
    }

    /**
     * Obtiene métricas del Circuit Breaker.
     *
     * @return Información de métricas
     */
    public CircuitBreakerMetrics getCircuitBreakerMetrics() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(MONGODB_INSTANCE);
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();

        return CircuitBreakerMetrics.builder()
                .state(circuitBreaker.getState().name())
                .failureRate(metrics.getFailureRate())
                .slowCallRate(metrics.getSlowCallRate())
                .numberOfSuccessfulCalls(metrics.getNumberOfSuccessfulCalls())
                .numberOfFailedCalls(metrics.getNumberOfFailedCalls())
                .numberOfSlowCalls(metrics.getNumberOfSlowCalls())
                .numberOfNotPermittedCalls(metrics.getNumberOfNotPermittedCalls())
                .build();
    }

    /**
     * Registra el estado actual del Circuit Breaker antes de ejecutar una operación.
     */
    private void logCircuitBreakerState(CircuitBreaker circuitBreaker, String operationName) {
        String state = circuitBreaker.getState().name();

        if (!"CLOSED".equals(state)) {
            log.warn("Circuit Breaker para '{}' está en estado: {}. Operación: {}",
                    MONGODB_INSTANCE, state, operationName);
        } else {
            log.debug("Ejecutando operación '{}' con Circuit Breaker en estado: {}",
                    operationName, state);
        }
    }

    /**
     * DTO con métricas del Circuit Breaker.
     */
    @lombok.Builder
    @lombok.Data
    public static class CircuitBreakerMetrics {
        private String state;
        private float failureRate;
        private float slowCallRate;
        private int numberOfSuccessfulCalls;
        private int numberOfFailedCalls;
        private int numberOfSlowCalls;
        private long numberOfNotPermittedCalls;
    }
}

