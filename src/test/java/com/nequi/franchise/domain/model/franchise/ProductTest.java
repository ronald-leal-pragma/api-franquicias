package com.nequi.franchise.domain.model.franchise;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitarios para el modelo Product")
class ProductTest {

    @Test
    @DisplayName("Debe crear un producto con builder exitosamente")
    void shouldCreateProductWithBuilder() {
        // Arrange & Act
        Product product = Product.builder()
                .name("Producto Test")
                .stock(100)
                .build();

        // Assert
        assertNotNull(product);
        assertEquals("Producto Test", product.getName());
        assertEquals(100, product.getStock());
    }

    @Test
    @DisplayName("Debe crear un producto con stock cero")
    void shouldCreateProductWithZeroStock() {
        // Arrange & Act
        Product product = Product.builder()
                .name("Producto Sin Stock")
                .stock(0)
                .build();

        // Assert
        assertNotNull(product);
        assertEquals("Producto Sin Stock", product.getName());
        assertEquals(0, product.getStock());
    }

    @Test
    @DisplayName("Debe crear un producto con stock nulo")
    void shouldCreateProductWithNullStock() {
        // Arrange & Act
        Product product = Product.builder()
                .name("Producto Sin Stock Definido")
                .stock(null)
                .build();

        // Assert
        assertNotNull(product);
        assertEquals("Producto Sin Stock Definido", product.getName());
        assertNull(product.getStock());
    }

    @Test
    @DisplayName("Debe crear un producto usando constructor sin argumentos")
    void shouldCreateProductWithNoArgsConstructor() {
        // Act
        Product product = new Product();

        // Assert
        assertNotNull(product);
        assertNull(product.getName());
        assertNull(product.getStock());
    }

    @Test
    @DisplayName("Debe crear un producto usando constructor con todos los argumentos")
    void shouldCreateProductWithAllArgsConstructor() {
        // Act
        Product product = new Product("product-456", "Producto Completo", 250);

        // Assert
        assertNotNull(product);
        assertEquals("product-456", product.getProductId());
        assertEquals("Producto Completo", product.getName());
        assertEquals(250, product.getStock());
    }

    @Test
    @DisplayName("Debe modificar propiedades usando setters")
    void shouldModifyPropertiesUsingSetters() {
        // Arrange
        Product product = new Product();

        // Act
        product.setName("Producto Modificado");
        product.setStock(500);

        // Assert
        assertEquals("Producto Modificado", product.getName());
        assertEquals(500, product.getStock());
    }

    @Test
    @DisplayName("Debe clonar un producto usando toBuilder")
    void shouldCloneProductUsingToBuilder() {
        // Arrange
        Product original = Product.builder()
                .name("Producto Original")
                .stock(100)
                .build();

        // Act
        Product cloned = original.toBuilder()
                .stock(200)
                .build();

        // Assert
        assertNotNull(cloned);
        assertEquals("Producto Original", cloned.getName());
        assertEquals(200, cloned.getStock());
        assertEquals(original.getName(), cloned.getName());
        assertNotEquals(original.getStock(), cloned.getStock());
    }

    @Test
    @DisplayName("Debe verificar equals y hashCode correctamente")
    void shouldVerifyEqualsAndHashCode() {
        // Arrange
        Product product1 = Product.builder()
                .name("Producto A")
                .stock(100)
                .build();

        Product product2 = Product.builder()
                .name("Producto A")
                .stock(100)
                .build();

        Product product3 = Product.builder()
                .name("Producto B")
                .stock(100)
                .build();

        // Assert
        assertEquals(product1, product2);
        assertNotEquals(product1, product3);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    @DisplayName("Debe permitir stock con valores grandes")
    void shouldAllowLargeStockValues() {
        // Arrange & Act
        Product product = Product.builder()
                .name("Producto con Stock Alto")
                .stock(Integer.MAX_VALUE)
                .build();

        // Assert
        assertNotNull(product);
        assertEquals(Integer.MAX_VALUE, product.getStock());
    }

    @Test
    @DisplayName("Debe manejar nombres vacios")
    void shouldHandleEmptyName() {
        // Arrange & Act
        Product product = Product.builder()
                .name("")
                .stock(50)
                .build();

        // Assert
        assertNotNull(product);
        assertEquals("", product.getName());
        assertEquals(50, product.getStock());
    }

    @Test
    @DisplayName("Debe crear producto con nombre y sin stock")
    void shouldCreateProductWithNameOnly() {
        // Arrange & Act
        Product product = Product.builder()
                .name("Solo Nombre")
                .build();

        // Assert
        assertNotNull(product);
        assertEquals("Solo Nombre", product.getName());
        assertNull(product.getStock());
    }
}

