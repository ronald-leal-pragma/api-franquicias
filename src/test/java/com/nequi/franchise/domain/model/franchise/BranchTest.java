package com.nequi.franchise.domain.model.franchise;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitarios para el modelo Branch")
class BranchTest {

    @Test
    @DisplayName("Debe crear una sucursal con builder exitosamente")
    void shouldCreateBranchWithBuilder() {
        // Arrange & Act
        Branch branch = Branch.builder()
                .name("Sucursal Centro")
                .build();

        // Assert
        assertNotNull(branch);
        assertEquals("Sucursal Centro", branch.getName());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    @DisplayName("Debe crear una sucursal con productos usando builder")
    void shouldCreateBranchWithProducts() {
        // Arrange
        Product product1 = Product.builder().name("Producto 1").stock(100).build();
        Product product2 = Product.builder().name("Producto 2").stock(200).build();
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);

        // Act
        Branch branch = Branch.builder()
                .name("Sucursal Norte")
                .products(products)
                .build();

        // Assert
        assertNotNull(branch);
        assertEquals("Sucursal Norte", branch.getName());
        assertEquals(2, branch.getProducts().size());
        assertEquals("Producto 1", branch.getProducts().get(0).getName());
        assertEquals("Producto 2", branch.getProducts().get(1).getName());
    }

    @Test
    @DisplayName("Debe crear una sucursal usando constructor sin argumentos")
    void shouldCreateBranchWithNoArgsConstructor() {
        // Act
        Branch branch = new Branch();

        // Assert
        assertNotNull(branch);
        assertNull(branch.getName());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    @DisplayName("Debe crear una sucursal usando constructor con todos los argumentos")
    void shouldCreateBranchWithAllArgsConstructor() {
        // Arrange
        List<Product> products = new ArrayList<>();
        products.add(Product.builder().name("Producto 1").stock(50).build());

        // Act
        Branch branch = new Branch("Sucursal Sur", products);

        // Assert
        assertNotNull(branch);
        assertEquals("Sucursal Sur", branch.getName());
        assertEquals(1, branch.getProducts().size());
    }

    @Test
    @DisplayName("Debe modificar propiedades usando setters")
    void shouldModifyPropertiesUsingSetters() {
        // Arrange
        Branch branch = new Branch();

        // Act
        branch.setName("Sucursal Modificada");
        branch.setProducts(new ArrayList<>());

        // Assert
        assertEquals("Sucursal Modificada", branch.getName());
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
    }

    @Test
    @DisplayName("Debe clonar una sucursal usando toBuilder")
    void shouldCloneBranchUsingToBuilder() {
        // Arrange
        Branch original = Branch.builder()
                .name("Sucursal Original")
                .build();

        // Act
        Branch cloned = original.toBuilder()
                .name("Sucursal Clonada")
                .build();

        // Assert
        assertNotNull(cloned);
        assertEquals("Sucursal Clonada", cloned.getName());
        assertNotEquals(original.getName(), cloned.getName());
    }

    @Test
    @DisplayName("Debe verificar equals y hashCode correctamente")
    void shouldVerifyEqualsAndHashCode() {
        // Arrange
        Branch branch1 = Branch.builder()
                .name("Sucursal Centro")
                .build();

        Branch branch2 = Branch.builder()
                .name("Sucursal Centro")
                .build();

        Branch branch3 = Branch.builder()
                .name("Sucursal Norte")
                .build();

        // Assert
        assertEquals(branch1, branch2);
        assertNotEquals(branch1, branch3);
        assertEquals(branch1.hashCode(), branch2.hashCode());
    }

    @Test
    @DisplayName("Debe agregar productos a la lista")
    void shouldAddProductsToList() {
        // Arrange
        Branch branch = Branch.builder()
                .name("Sucursal Centro")
                .build();

        Product product = Product.builder().name("Nuevo Producto").stock(10).build();

        // Act
        branch.getProducts().add(product);

        // Assert
        assertEquals(1, branch.getProducts().size());
        assertEquals("Nuevo Producto", branch.getProducts().get(0).getName());
        assertEquals(10, branch.getProducts().get(0).getStock());
    }

    @Test
    @DisplayName("Debe manejar lista vacia de productos por defecto")
    void shouldHandleEmptyProductsListByDefault() {
        // Arrange & Act
        Branch branch = Branch.builder()
                .name("Sucursal Este")
                .build();

        // Assert
        assertNotNull(branch.getProducts());
        assertTrue(branch.getProducts().isEmpty());
        assertEquals(0, branch.getProducts().size());
    }
}

