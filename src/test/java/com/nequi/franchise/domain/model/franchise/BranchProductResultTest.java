package com.nequi.franchise.domain.model.franchise;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitarios para el modelo BranchProductResult")
class BranchProductResultTest {

    @Test
    @DisplayName("Debe crear un resultado con builder exitosamente")
    void shouldCreateResultWithBuilder() {
        // Arrange
        Product product = Product.builder()
                .name("Producto Test")
                .stock(100)
                .build();

        // Act
        BranchProductResult result = BranchProductResult.builder()
                .branchName("Sucursal Centro")
                .product(product)
                .build();

        // Assert
        assertNotNull(result);
        assertEquals("Sucursal Centro", result.getBranchName());
        assertNotNull(result.getProduct());
        assertEquals("Producto Test", result.getProduct().getName());
        assertEquals(100, result.getProduct().getStock());
    }

    @Test
    @DisplayName("Debe crear un resultado usando constructor sin argumentos")
    void shouldCreateResultWithNoArgsConstructor() {
        // Act
        BranchProductResult result = new BranchProductResult();

        // Assert
        assertNotNull(result);
        assertNull(result.getBranchName());
        assertNull(result.getProduct());
    }

    @Test
    @DisplayName("Debe crear un resultado usando constructor con todos los argumentos")
    void shouldCreateResultWithAllArgsConstructor() {
        // Arrange
        Product product = Product.builder()
                .name("Producto Completo")
                .stock(250)
                .build();

        // Act
        BranchProductResult result = new BranchProductResult("Sucursal Norte", product);

        // Assert
        assertNotNull(result);
        assertEquals("Sucursal Norte", result.getBranchName());
        assertEquals("Producto Completo", result.getProduct().getName());
        assertEquals(250, result.getProduct().getStock());
    }

    @Test
    @DisplayName("Debe modificar propiedades usando setters")
    void shouldModifyPropertiesUsingSetters() {
        // Arrange
        BranchProductResult result = new BranchProductResult();
        Product product = Product.builder()
                .name("Nuevo Producto")
                .stock(50)
                .build();

        // Act
        result.setBranchName("Sucursal Sur");
        result.setProduct(product);

        // Assert
        assertEquals("Sucursal Sur", result.getBranchName());
        assertNotNull(result.getProduct());
        assertEquals("Nuevo Producto", result.getProduct().getName());
    }

    @Test
    @DisplayName("Debe verificar equals y hashCode correctamente")
    void shouldVerifyEqualsAndHashCode() {
        // Arrange
        Product product = Product.builder()
                .name("Producto A")
                .stock(100)
                .build();

        BranchProductResult result1 = BranchProductResult.builder()
                .branchName("Sucursal Centro")
                .product(product)
                .build();

        BranchProductResult result2 = BranchProductResult.builder()
                .branchName("Sucursal Centro")
                .product(product)
                .build();

        BranchProductResult result3 = BranchProductResult.builder()
                .branchName("Sucursal Norte")
                .product(product)
                .build();

        // Assert
        assertEquals(result1, result2);
        assertNotEquals(result1, result3);
        assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    @DisplayName("Debe permitir producto nulo")
    void shouldAllowNullProduct() {
        // Arrange & Act
        BranchProductResult result = BranchProductResult.builder()
                .branchName("Sucursal Este")
                .product(null)
                .build();

        // Assert
        assertNotNull(result);
        assertEquals("Sucursal Este", result.getBranchName());
        assertNull(result.getProduct());
    }

    @Test
    @DisplayName("Debe permitir nombre de sucursal nulo")
    void shouldAllowNullBranchName() {
        // Arrange
        Product product = Product.builder()
                .name("Producto X")
                .stock(75)
                .build();

        // Act
        BranchProductResult result = BranchProductResult.builder()
                .branchName(null)
                .product(product)
                .build();

        // Assert
        assertNotNull(result);
        assertNull(result.getBranchName());
        assertNotNull(result.getProduct());
        assertEquals("Producto X", result.getProduct().getName());
    }

    @Test
    @DisplayName("Debe manejar nombres de sucursal vacios")
    void shouldHandleEmptyBranchName() {
        // Arrange
        Product product = Product.builder()
                .name("Producto Y")
                .stock(200)
                .build();

        // Act
        BranchProductResult result = BranchProductResult.builder()
                .branchName("")
                .product(product)
                .build();

        // Assert
        assertNotNull(result);
        assertEquals("", result.getBranchName());
        assertNotNull(result.getProduct());
    }

    @Test
    @DisplayName("Debe representar correctamente la relacion sucursal-producto")
    void shouldRepresentBranchProductRelationshipCorrectly() {
        // Arrange
        Product product = Product.builder()
                .name("Producto con Mayor Stock")
                .stock(500)
                .build();

        // Act
        BranchProductResult result = BranchProductResult.builder()
                .branchName("Sucursal Oeste")
                .product(product)
                .build();

        // Assert
        assertNotNull(result);
        assertEquals("Sucursal Oeste", result.getBranchName());
        assertEquals("Producto con Mayor Stock", result.getProduct().getName());
        assertEquals(500, result.getProduct().getStock());
    }
}

