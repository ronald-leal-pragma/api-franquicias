package com.nequi.franchise.domain.usecase.franchise;

import com.nequi.franchise.domain.model.franchise.Franchise;
import com.nequi.franchise.domain.model.franchise.Product;
import com.nequi.franchise.domain.model.gateway.FranchiseGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para AddProductUseCase")
class AddProductUseCaseTest {

    @Mock
    private FranchiseGateway gateway;

    @InjectMocks
    private AddProductUseCase addProductUseCase;

    private Product product;
    private Franchise franchise;
    private String franchiseId;
    private String branchName;

    @BeforeEach
    void setUp() {
        franchiseId = "franchise-1";
        branchName = "Sucursal Centro";

        product = Product.builder()
                .name("Producto Test")
                .stock(100)
                .build();

        franchise = Franchise.builder()
                .id(franchiseId)
                .name("Franquicia Test")
                .build();
    }

    @Test
    @DisplayName("Debe agregar un producto exitosamente con stock valido")
    void shouldAddProductSuccessfully() {
        // Arrange
        when(gateway.addProduct(eq(franchiseId), eq(branchName), any(Product.class)))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = addProductUseCase.apply(franchiseId, branchName, product);

        // Assert
        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(gateway, times(1)).addProduct(franchiseId, branchName, product);
    }

    @Test
    @DisplayName("Debe agregar un producto exitosamente con stock nulo")
    void shouldAddProductSuccessfullyWhenStockIsNull() {
        // Arrange
        product.setStock(null);
        when(gateway.addProduct(eq(franchiseId), eq(branchName), any(Product.class)))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = addProductUseCase.apply(franchiseId, branchName, product);

        // Assert
        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(gateway, times(1)).addProduct(franchiseId, branchName, product);
    }

    @Test
    @DisplayName("Debe agregar un producto exitosamente con stock cero")
    void shouldAddProductSuccessfullyWhenStockIsZero() {
        // Arrange
        product.setStock(0);
        when(gateway.addProduct(eq(franchiseId), eq(branchName), any(Product.class)))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = addProductUseCase.apply(franchiseId, branchName, product);

        // Assert
        StepVerifier.create(result)
                .expectNext(franchise)
                .verifyComplete();

        verify(gateway, times(1)).addProduct(franchiseId, branchName, product);
    }

    @Test
    @DisplayName("Debe lanzar error cuando el stock es negativo")
    void shouldThrowErrorWhenStockIsNegative() {
        // Arrange
        product.setStock(-1);

        // Act
        Mono<Franchise> result = addProductUseCase.apply(franchiseId, branchName, product);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Stock cannot be negative"))
                .verify();

        verify(gateway, never()).addProduct(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Debe lanzar error cuando el stock es un numero negativo grande")
    void shouldThrowErrorWhenStockIsLargeNegative() {
        // Arrange
        product.setStock(-999);

        // Act
        Mono<Franchise> result = addProductUseCase.apply(franchiseId, branchName, product);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Stock cannot be negative"))
                .verify();

        verify(gateway, never()).addProduct(anyString(), anyString(), any());
    }
}

