package com.nequi.franchise.infrastructure.entrypoints.reactiveweb;

import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.usecase.franchise.*;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Tag(name = "Franquicias", description = "API para gestionar franquicias, sucursales y productos")
public class FranchiseHandler {
    private static final String BASE_PATH = "/api/franchises";
    private static final String FRANCHISE_ID = "franchiseId";
    private static final String BRANCH_NAME = "branchName";
    private static final String PRODUCT_NAME = "productName";

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final AddProductUseCase addProductUseCase;
    private final RemoveProductUseCase removeProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;
    private final FindMaxStockUseCase findMaxStockUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final GlobalErrorHandler errorHandler;

    @Operation(
            summary = "Crear una nueva franquicia",
            description = "Crea una nueva franquicia en el sistema con el nombre proporcionado",
            requestBody = @RequestBody(
                    description = "Datos de la franquicia a crear",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FranchiseRequest.class),
                            examples = @ExampleObject(
                                    name = "Nueva Franquicia",
                                    value = "{\"name\": \"Franquicia El Éxito\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Franquicia creada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Franchise.class),
                                    examples = @ExampleObject(
                                            value = "{\"id\": \"123\", \"name\": \"Franquicia El Éxito\", \"branches\": []}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación - nombre vacío o inválido",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"code\": \"VALIDATION_ERROR\", \"message\": \"El nombre de la franquicia no puede estar vacío\", \"timestamp\": \"2026-02-15T10:30:00\", \"path\": \"/api/franchises\"}"
                                    )
                            )
                    )
            }
    )
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .flatMap(dto -> {
                    Franchise franchiseDomain = Franchise.builder()
                            .name(dto.getName())
                            .build();

                    return createFranchiseUseCase.apply(franchiseDomain);
                })
                .flatMap(savedFranchise -> ServerResponse
                        .created(URI.create(BASE_PATH + savedFranchise.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedFranchise)
                )
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    @Operation(
            summary = "Agregar sucursal a una franquicia",
            description = "Agrega una nueva sucursal a una franquicia existente",
            parameters = {
                    @Parameter(
                            name = "franchiseId",
                            description = "ID de la franquicia",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "string"),
                            example = "123"
                    )
            },
            requestBody = @RequestBody(
                    description = "Datos de la sucursal a agregar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddBranchRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"name\": \"Sucursal Centro\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sucursal agregada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Franchise.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación - nombre de sucursal vacío",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Franquicia no encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"code\": \"RESOURCE_NOT_FOUND\", \"message\": \"Franquicia no encontrada con ID: 123\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflicto - Ya existe una sucursal con ese nombre",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"code\": \"BUSINESS_ERROR\", \"message\": \"Ya existe una sucursal con el nombre 'Sucursal Centro' en esta franquicia\"}"
                                    )
                            )
                    )
            }
    )
    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);

        return request.bodyToMono(AddBranchRequest.class)
                .flatMap(dto -> {
                    Branch branch = Branch.builder().name(dto.getName()).build();
                    return addBranchUseCase.apply(franchiseId, branch);
                })
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    @Operation(
            summary = "Agregar producto a una sucursal",
            description = "Agrega un nuevo producto al inventario de una sucursal específica",
            parameters = {
                    @Parameter(
                            name = "franchiseId",
                            description = "ID de la franquicia",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "123"
                    ),
                    @Parameter(
                            name = "branchName",
                            description = "Nombre de la sucursal",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "Sucursal Centro"
                    )
            },
            requestBody = @RequestBody(
                    description = "Datos del producto a agregar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddProductRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"name\": \"Laptop Dell\", \"stock\": 50}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Producto agregado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Franchise.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación - stock negativo",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"code\": \"VALIDATION_ERROR\", \"message\": \"El stock no puede ser negativo\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Franquicia o sucursal no encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);

        return request.bodyToMono(AddProductRequest.class)
                .flatMap(dto -> {
                    Product product = Product.builder()
                            .name(dto.getName())
                            .stock(dto.getStock())
                            .build();
                    return addProductUseCase.apply(franchiseId, branchName, product);
                })
                .flatMap(saved -> ServerResponse.ok().bodyValue(saved))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    @Operation(
            summary = "Eliminar producto de una sucursal",
            description = "Elimina un producto del inventario de una sucursal específica",
            parameters = {
                    @Parameter(
                            name = "franchiseId",
                            description = "ID de la franquicia",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "123"
                    ),
                    @Parameter(
                            name = "branchName",
                            description = "Nombre de la sucursal",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "Sucursal Centro"
                    ),
                    @Parameter(
                            name = "productName",
                            description = "Nombre del producto a eliminar",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "Laptop Dell"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Producto eliminado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Franchise.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Franquicia, sucursal o producto no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public Mono<ServerResponse> removeProduct(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        String productName = request.pathVariable(PRODUCT_NAME);

        return removeProductUseCase.apply(franchiseId, branchName, productName)
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    @Operation(
            summary = "Actualizar stock de un producto",
            description = "Actualiza la cantidad de stock de un producto específico en una sucursal",
            parameters = {
                    @Parameter(
                            name = "franchiseId",
                            description = "ID de la franquicia",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "123"
                    ),
                    @Parameter(
                            name = "branchName",
                            description = "Nombre de la sucursal",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "Sucursal Centro"
                    ),
                    @Parameter(
                            name = "productName",
                            description = "Nombre del producto",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "Laptop Dell"
                    )
            },
            requestBody = @RequestBody(
                    description = "Nueva cantidad de stock",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateStockRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"stock\": 75}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stock actualizado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Franchise.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación - stock negativo",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"code\": \"VALIDATION_ERROR\", \"message\": \"El stock no puede ser negativo\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Producto no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        String productName = request.pathVariable(PRODUCT_NAME);

        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(dto -> updateStockUseCase.apply(franchiseId, branchName, productName, dto.getStock()))
                .flatMap(updatedFranchise -> ServerResponse.ok().bodyValue(updatedFranchise))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    @Operation(
            summary = "Obtener productos con mayor stock por sucursal",
            description = "Retorna el producto con mayor stock de cada sucursal de una franquicia específica",
            parameters = {
                    @Parameter(
                            name = "franchiseId",
                            description = "ID de la franquicia",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "123"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de productos con mayor stock por sucursal",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BranchProductResult.class),
                                    examples = @ExampleObject(
                                            value = "[{\"branchName\": \"Sucursal Centro\", \"product\": {\"name\": \"Laptop Dell\", \"stock\": 100}}, " +
                                                    "{\"branchName\": \"Sucursal Norte\", \"product\": {\"name\": \"Mouse Logitech\", \"stock\": 200}}]"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Franquicia no encontrada o sin productos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID);

        return ServerResponse.ok()
                .body(findMaxStockUseCase.apply(franchiseId), BranchProductResult.class)
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    @Operation(
            summary = "Actualizar nombre de franquicia",
            description = "Actualiza el nombre de una franquicia existente",
            parameters = {
                    @Parameter(
                            name = "franchiseId",
                            description = "ID de la franquicia",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "123"
                    )
            },
            requestBody = @RequestBody(
                    description = "Nuevo nombre de la franquicia",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateNameRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"name\": \"Franquicia El Éxito Renovado\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Nombre actualizado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Franchise.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación - nombre vacío",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Franquicia no encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String id = request.pathVariable(FRANCHISE_ID);
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateFranchiseNameUseCase.apply(id, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    @Operation(
            summary = "Actualizar nombre de sucursal",
            description = "Actualiza el nombre de una sucursal específica",
            parameters = {
                    @Parameter(
                            name = "franchiseId",
                            description = "ID de la franquicia",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "123"
                    ),
                    @Parameter(
                            name = "branchName",
                            description = "Nombre actual de la sucursal",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "Sucursal Centro"
                    )
            },
            requestBody = @RequestBody(
                    description = "Nuevo nombre de la sucursal",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateNameRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"name\": \"Sucursal Centro Renovada\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Nombre actualizado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Franchise.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación - nombre vacío",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Franquicia o sucursal no encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String id = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateBranchNameUseCase.apply(id, branchName, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }

    @Operation(
            summary = "Actualizar nombre de producto",
            description = "Actualiza el nombre de un producto específico en una sucursal",
            parameters = {
                    @Parameter(
                            name = "franchiseId",
                            description = "ID de la franquicia",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "123"
                    ),
                    @Parameter(
                            name = "branchName",
                            description = "Nombre de la sucursal",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "Sucursal Centro"
                    ),
                    @Parameter(
                            name = "productName",
                            description = "Nombre actual del producto",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "Laptop Dell"
                    )
            },
            requestBody = @RequestBody(
                    description = "Nuevo nombre del producto",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateNameRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"name\": \"Laptop Dell XPS 15\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Nombre actualizado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Franchise.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Error de validación - nombre vacío",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Producto no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String id = request.pathVariable(FRANCHISE_ID);
        String branchName = request.pathVariable(BRANCH_NAME);
        String productName = request.pathVariable(PRODUCT_NAME);

        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(dto -> updateProductNameUseCase.apply(id, branchName, productName, dto.getName()))
                .flatMap(f -> ServerResponse.ok().bodyValue(f))
                .onErrorResume(error -> errorHandler.handleError(error, request));
    }
}
