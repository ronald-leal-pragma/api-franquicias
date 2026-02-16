package com.nequi.franchise.application.config;

import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.FranchiseHandler;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.ResilienceHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @RouterOperations({
            @RouterOperation(
                    path = "/api/franchises",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Crear una nueva franquicia",
                            description = "Crea una nueva franquicia en el sistema. El nombre debe ser único",
                            tags = {"Franquicias"},
                            requestBody = @RequestBody(
                                    description = "Datos de la franquicia a crear",
                                    required = true,
                                    content = @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.FranchiseRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franquicia creada exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "409", description = "Ya existe una franquicia con ese nombre")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches",
                    operation = @Operation(
                            operationId = "addBranch",
                            summary = "Agregar sucursal a franquicia",
                            description = "Agrega una nueva sucursal a una franquicia existente",
                            tags = {"Franquicias"},
                            parameters = {
                                    @Parameter(
                                            name = "franchiseId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "ID de la franquicia",
                                            example = "507f1f77bcf86cd799439011"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Datos de la sucursal a agregar",
                                    required = true,
                                    content = @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.AddBranchRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Sucursal agregada exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada"),
                                    @ApiResponse(responseCode = "409", description = "Ya existe una sucursal con ese nombre")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches/{branchName}/products",
                    operation = @Operation(
                            operationId = "addProduct",
                            summary = "Agregar producto a sucursal",
                            description = "Agrega un nuevo producto al inventario de una sucursal",
                            tags = {"Franquicias"},
                            parameters = {
                                    @Parameter(
                                            name = "franchiseId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "ID de la franquicia",
                                            example = "507f1f77bcf86cd799439011"
                                    ),
                                    @Parameter(
                                            name = "branchName",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Nombre de la sucursal",
                                            example = "Sucursal Centro"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Datos del producto a agregar (nombre y stock inicial)",
                                    required = true,
                                    content = @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.AddProductRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Producto agregado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos (stock negativo, nombre vacío)"),
                                    @ApiResponse(responseCode = "404", description = "Franquicia o sucursal no encontrada"),
                                    @ApiResponse(responseCode = "409", description = "Ya existe un producto con ese nombre")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}",
                    operation = @Operation(
                            operationId = "removeProduct",
                            summary = "Eliminar producto de sucursal",
                            description = "Elimina un producto del inventario de una sucursal específica",
                            tags = {"Franquicias"},
                            parameters = {
                                    @Parameter(
                                            name = "franchiseId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "ID de la franquicia",
                                            example = "507f1f77bcf86cd799439011"
                                    ),
                                    @Parameter(
                                            name = "branchName",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Nombre de la sucursal",
                                            example = "Sucursal Centro"
                                    ),
                                    @Parameter(
                                            name = "productName",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Nombre del producto a eliminar",
                                            example = "Producto A"
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
                                    @ApiResponse(responseCode = "404", description = "Franquicia, sucursal o producto no encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}",
                    operation = @Operation(
                            operationId = "updateStock",
                            summary = "Actualizar stock de producto",
                            description = "Modifica la cantidad en stock de un producto específico",
                            tags = {"Franquicias"},
                            parameters = {
                                    @Parameter(
                                            name = "franchiseId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "ID de la franquicia",
                                            example = "507f1f77bcf86cd799439011"
                                    ),
                                    @Parameter(
                                            name = "branchName",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Nombre de la sucursal",
                                            example = "Sucursal Centro"
                                    ),
                                    @Parameter(
                                            name = "productName",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Nombre del producto",
                                            example = "Producto A"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Nuevo valor de stock",
                                    required = true,
                                    content = @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.UpdateStockRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Stock inválido (no puede ser negativo)"),
                                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/products/max-stock",
                    operation = @Operation(
                            operationId = "getMaxStockProducts",
                            summary = "Obtener productos con mayor stock por sucursal",
                            description = "Retorna el producto con mayor stock de cada sucursal de la franquicia",
                            tags = {"Franquicias"},
                            parameters = {
                                    @Parameter(
                                            name = "franchiseId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "ID de la franquicia",
                                            example = "507f1f77bcf86cd799439011"
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de productos con mayor stock por sucursal"),
                                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}",
                    operation = @Operation(
                            operationId = "updateFranchiseName",
                            summary = "Actualizar nombre de franquicia",
                            tags = {"Franquicias"},
                            parameters = {
                                    @Parameter(
                                            name = "franchiseId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "ID de la franquicia",
                                            example = "507f1f77bcf86cd799439011"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Datos con el nuevo nombre de la franquicia",
                                    required = true,
                                    content = @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.UpdateNameRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Nombre actualizado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada"),
                                    @ApiResponse(responseCode = "409", description = "Ya existe una franquicia con ese nombre")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches/{branchName}",
                    operation = @Operation(
                            operationId = "updateBranchName",
                            summary = "Actualizar nombre de sucursal",
                            tags = {"Franquicias"},
                            parameters = {
                                    @Parameter(
                                            name = "franchiseId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "ID de la franquicia",
                                            example = "507f1f77bcf86cd799439011"
                                    ),
                                    @Parameter(
                                            name = "branchName",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Nombre actual de la sucursal",
                                            example = "Sucursal Centro"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Datos con el nuevo nombre de la sucursal",
                                    required = true,
                                    content = @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.UpdateNameRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Nombre actualizado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "404", description = "Franquicia o sucursal no encontrada"),
                                    @ApiResponse(responseCode = "409", description = "Ya existe una sucursal con ese nombre")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}/rename",
                    operation = @Operation(
                            operationId = "updateProductName",
                            summary = "Actualizar nombre de producto",
                            tags = {"Franquicias"},
                            parameters = {
                                    @Parameter(
                                            name = "franchiseId",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "ID de la franquicia",
                                            example = "507f1f77bcf86cd799439011"
                                    ),
                                    @Parameter(
                                            name = "branchName",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Nombre de la sucursal",
                                            example = "Sucursal Centro"
                                    ),
                                    @Parameter(
                                            name = "productName",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Nombre actual del producto",
                                            example = "Producto A"
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Datos con el nuevo nombre del producto",
                                    required = true,
                                    content = @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.UpdateNameRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Nombre actualizado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
                                    @ApiResponse(responseCode = "409", description = "Ya existe un producto con ese nombre")
                            }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return route(POST("/api/franchises"), handler::createFranchise)
                .andRoute(POST("/api/franchises/{franchiseId}/branches"), handler::addBranch)
                .andRoute(POST("/api/franchises/{franchiseId}/branches/{branchName}/products"), handler::addProduct)
                .andRoute(DELETE("/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}"),
                        handler::removeProduct)
                .andRoute(PATCH("/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}"), handler::updateStock)
                .andRoute(GET("/api/franchises/{franchiseId}/products/max-stock"), handler::getMaxStockProducts)
                .andRoute(PATCH("/api/franchises/{franchiseId}"), handler::updateFranchiseName)
                .andRoute(PATCH("/api/franchises/{franchiseId}/branches/{branchName}"), handler::updateBranchName)
                .andRoute(PATCH("/api/franchises/{franchiseId}/branches/{branchName}/products/{productName}/rename"), handler::updateProductName);
    }

    @RouterOperations({
            @RouterOperation(
                    path = "/api/monitoring/circuit-breaker/state",
                    operation = @Operation(
                            operationId = "getCircuitBreakerState",
                            summary = "Obtener estado del Circuit Breaker",
                            tags = {"Monitoreo"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Estado obtenido")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/monitoring/circuit-breaker/metrics",
                    operation = @Operation(
                            operationId = "getCircuitBreakerMetrics",
                            summary = "Obtener métricas del Circuit Breaker",
                            tags = {"Monitoreo"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Métricas obtenidas")
                            }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> resilienceRoutes(ResilienceHandler handler) {
        return route(GET("/api/monitoring/circuit-breaker/state"), handler::getCircuitBreakerState)
                .andRoute(GET("/api/monitoring/circuit-breaker/metrics"), handler::getCircuitBreakerMetrics);
    }
}

