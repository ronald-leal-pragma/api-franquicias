package com.nequi.franchise.infrastructure.entrypoints.reactiveweb;

import com.nequi.franchise.domain.model.franchise.Branch;
import com.nequi.franchise.domain.model.franchise.BranchProductResult;
import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.usecase.franchise.*;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.dto.*;
import com.nequi.franchise.infrastructure.entrypoints.reactiveweb.mapper.FranchiseDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para FranchiseHandler")
class FranchiseHandlerTest {

    @Mock
    private CreateFranchiseUseCase createFranchiseUseCase;
    @Mock
    private AddBranchUseCase addBranchUseCase;
    @Mock
    private AddProductUseCase addProductUseCase;
    @Mock
    private RemoveProductUseCase removeProductUseCase;
    @Mock
    private UpdateStockUseCase updateStockUseCase;
    @Mock
    private FindMaxStockUseCase findMaxStockUseCase;
    @Mock
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    @Mock
    private UpdateBranchNameUseCase updateBranchNameUseCase;
    @Mock
    private UpdateProductNameUseCase updateProductNameUseCase;
    @Mock
    private GlobalErrorHandler errorHandler;
    @Mock
    private FranchiseDtoMapper mapper;

    @InjectMocks
    private FranchiseHandler handler;

    private Franchise franchise;
    private Branch branch;
    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .name("Laptop Dell")
                .stock(100)
                .build();

        branch = Branch.builder()
                .name("Sucursal Centro")
                .products(new ArrayList<>(Collections.singletonList(product)))
                .build();

        franchise = Franchise.builder()
                .id("123")
                .name("Franquicia Test")
                .branches(new ArrayList<>(Collections.singletonList(branch)))
                .build();
    }

    @Test
    @DisplayName("Debe crear franquicia exitosamente")
    void shouldCreateFranchiseSuccessfully() {
        // Arrange
        FranchiseRequest request = FranchiseRequest.builder()
                .name("Nueva Franquicia")
                .build();

        Franchise domainFranchise = Franchise.builder()
                .name("Nueva Franquicia")
                .build();

        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        when(mapper.toFranchise(request)).thenReturn(domainFranchise);
        when(createFranchiseUseCase.apply(domainFranchise)).thenReturn(Mono.just(franchise));

        // Act
        Mono<ServerResponse> result = handler.createFranchise(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.CREATED)
                .verifyComplete();

        verify(mapper).toFranchise(request);
        verify(createFranchiseUseCase).apply(domainFranchise);
    }

    @Test
    @DisplayName("Debe agregar sucursal exitosamente")
    void shouldAddBranchSuccessfully() {
        // Arrange
        AddBranchRequest request = new AddBranchRequest();
        request.setName("Nueva Sucursal");

        Branch newBranch = Branch.builder().name("Nueva Sucursal").build();

        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "123")
                .body(Mono.just(request));

        when(mapper.toBranch(request)).thenReturn(newBranch);
        when(addBranchUseCase.apply("123", newBranch)).thenReturn(Mono.just(franchise));

        // Act
        Mono<ServerResponse> result = handler.addBranch(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(mapper).toBranch(request);
        verify(addBranchUseCase).apply("123", newBranch);
    }

    @Test
    @DisplayName("Debe agregar producto exitosamente")
    void shouldAddProductSuccessfully() {
        // Arrange
        AddProductRequest request = new AddProductRequest();
        request.setName("Mouse Logitech");
        request.setStock(50);

        Product newProduct = Product.builder()
                .name("Mouse Logitech")
                .stock(50)
                .build();

        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "123")
                .pathVariable("branchName", "Sucursal Centro")
                .body(Mono.just(request));

        when(mapper.toProduct(request)).thenReturn(newProduct);
        when(addProductUseCase.apply("123", "Sucursal Centro", newProduct)).thenReturn(Mono.just(franchise));

        // Act
        Mono<ServerResponse> result = handler.addProduct(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(mapper).toProduct(request);
        verify(addProductUseCase).apply("123", "Sucursal Centro", newProduct);
    }

    @Test
    @DisplayName("Debe eliminar producto exitosamente")
    void shouldRemoveProductSuccessfully() {
        // Arrange
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "123")
                .pathVariable("branchName", "Sucursal Centro")
                .pathVariable("productName", "Laptop Dell")
                .build();

        when(removeProductUseCase.apply("123", "Sucursal Centro", "Laptop Dell"))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<ServerResponse> result = handler.removeProduct(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(removeProductUseCase).apply("123", "Sucursal Centro", "Laptop Dell");
    }

    @Test
    @DisplayName("Debe actualizar stock exitosamente")
    void shouldUpdateStockSuccessfully() {
        // Arrange
        UpdateStockRequest request = new UpdateStockRequest();
        request.setStock(200);

        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "123")
                .pathVariable("branchName", "Sucursal Centro")
                .pathVariable("productName", "Laptop Dell")
                .body(Mono.just(request));

        when(updateStockUseCase.apply("123", "Sucursal Centro", "Laptop Dell", 200))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<ServerResponse> result = handler.updateStock(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateStockUseCase).apply("123", "Sucursal Centro", "Laptop Dell", 200);
    }

    @Test
    @DisplayName("Debe obtener productos con mayor stock")
    void shouldGetMaxStockProducts() {
        // Arrange
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "123")
                .build();

        BranchProductResult result1 = new BranchProductResult("Sucursal Centro", product);
        BranchProductResult result2 = new BranchProductResult("Sucursal Norte", product);

        when(findMaxStockUseCase.apply("123"))
                .thenReturn(Flux.just(result1, result2));

        // Act
        Mono<ServerResponse> result = handler.getMaxStockProducts(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(findMaxStockUseCase).apply("123");
    }

    @Test
    @DisplayName("Debe actualizar nombre de franquicia exitosamente")
    void shouldUpdateFranchiseNameSuccessfully() {
        // Arrange
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Franquicia Actualizada");

        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "123")
                .body(Mono.just(request));

        when(updateFranchiseNameUseCase.apply("123", "Franquicia Actualizada"))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<ServerResponse> result = handler.updateFranchiseName(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateFranchiseNameUseCase).apply("123", "Franquicia Actualizada");
    }

    @Test
    @DisplayName("Debe actualizar nombre de sucursal exitosamente")
    void shouldUpdateBranchNameSuccessfully() {
        // Arrange
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Sucursal Actualizada");

        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "123")
                .pathVariable("branchName", "Sucursal Centro")
                .body(Mono.just(request));

        when(updateBranchNameUseCase.apply("123", "Sucursal Centro", "Sucursal Actualizada"))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<ServerResponse> result = handler.updateBranchName(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateBranchNameUseCase).apply("123", "Sucursal Centro", "Sucursal Actualizada");
    }

    @Test
    @DisplayName("Debe actualizar nombre de producto exitosamente")
    void shouldUpdateProductNameSuccessfully() {
        // Arrange
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Laptop HP");

        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "123")
                .pathVariable("branchName", "Sucursal Centro")
                .pathVariable("productName", "Laptop Dell")
                .body(Mono.just(request));

        when(updateProductNameUseCase.apply("123", "Sucursal Centro", "Laptop Dell", "Laptop HP"))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<ServerResponse> result = handler.updateProductName(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.OK)
                .verifyComplete();

        verify(updateProductNameUseCase).apply("123", "Sucursal Centro", "Laptop Dell", "Laptop HP");
    }

    @Test
    @DisplayName("Debe manejar error al crear franquicia")
    void shouldHandleErrorWhenCreatingFranchise() {
        // Arrange
        FranchiseRequest request = FranchiseRequest.builder()
                .name("Nueva Franquicia")
                .build();

        Franchise domainFranchise = Franchise.builder()
                .name("Nueva Franquicia")
                .build();

        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        RuntimeException exception = new RuntimeException("Error de prueba");

        when(mapper.toFranchise(request)).thenReturn(domainFranchise);
        when(createFranchiseUseCase.apply(domainFranchise)).thenReturn(Mono.error(exception));
        when(errorHandler.handleError(exception, serverRequest))
                .thenReturn(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        // Act
        Mono<ServerResponse> result = handler.createFranchise(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                .verifyComplete();

        verify(errorHandler).handleError(exception, serverRequest);
    }

    @Test
    @DisplayName("Debe retornar 404 cuando no hay productos con mayor stock")
    void shouldReturn404WhenNoMaxStockProducts() {
        // Arrange
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "123")
                .build();

        when(findMaxStockUseCase.apply("123")).thenReturn(Flux.empty());

        // Act
        Mono<ServerResponse> result = handler.getMaxStockProducts(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();

        verify(findMaxStockUseCase).apply("123");
    }
}


