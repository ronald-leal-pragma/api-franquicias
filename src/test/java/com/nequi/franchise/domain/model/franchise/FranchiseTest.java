package com.nequi.franchise.domain.model.franchise;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitarios para el modelo Franchise")
class FranchiseTest {

    @Test
    @DisplayName("Debe crear una franquicia con builder exitosamente")
    void shouldCreateFranchiseWithBuilder() {
        // Arrange & Act
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Franquicia Test")
                .build();

        // Assert
        assertNotNull(franchise);
        assertEquals("1", franchise.getId());
        assertEquals("Franquicia Test", franchise.getName());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Debe crear una franquicia con sucursales usando builder")
    void shouldCreateFranchiseWithBranches() {
        // Arrange
        Branch branch1 = Branch.builder().name("Sucursal 1").build();
        Branch branch2 = Branch.builder().name("Sucursal 2").build();
        List<Branch> branches = new ArrayList<>();
        branches.add(branch1);
        branches.add(branch2);

        // Act
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Franquicia Test")
                .branches(branches)
                .build();

        // Assert
        assertNotNull(franchise);
        assertEquals(2, franchise.getBranches().size());
        assertEquals("Sucursal 1", franchise.getBranches().get(0).getName());
        assertEquals("Sucursal 2", franchise.getBranches().get(1).getName());
    }

    @Test
    @DisplayName("Debe crear una franquicia usando constructor sin argumentos")
    void shouldCreateFranchiseWithNoArgsConstructor() {
        // Act
        Franchise franchise = new Franchise();

        // Assert
        assertNotNull(franchise);
        assertNull(franchise.getId());
        assertNull(franchise.getName());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Debe crear una franquicia usando constructor con todos los argumentos")
    void shouldCreateFranchiseWithAllArgsConstructor() {
        // Arrange
        List<Branch> branches = new ArrayList<>();
        branches.add(Branch.builder().name("Sucursal 1").build());

        // Act
        Franchise franchise = new Franchise("1", "Franquicia Test", branches);

        // Assert
        assertNotNull(franchise);
        assertEquals("1", franchise.getId());
        assertEquals("Franquicia Test", franchise.getName());
        assertEquals(1, franchise.getBranches().size());
    }

    @Test
    @DisplayName("Debe modificar propiedades usando setters")
    void shouldModifyPropertiesUsingSetters() {
        // Arrange
        Franchise franchise = new Franchise();

        // Act
        franchise.setId("123");
        franchise.setName("Nueva Franquicia");
        franchise.setBranches(new ArrayList<>());

        // Assert
        assertEquals("123", franchise.getId());
        assertEquals("Nueva Franquicia", franchise.getName());
        assertNotNull(franchise.getBranches());
        assertTrue(franchise.getBranches().isEmpty());
    }

    @Test
    @DisplayName("Debe clonar una franquicia usando toBuilder")
    void shouldCloneFranchiseUsingToBuilder() {
        // Arrange
        Franchise original = Franchise.builder()
                .id("1")
                .name("Franquicia Original")
                .build();

        // Act
        Franchise cloned = original.toBuilder()
                .name("Franquicia Clonada")
                .build();

        // Assert
        assertNotNull(cloned);
        assertEquals("1", cloned.getId());
        assertEquals("Franquicia Clonada", cloned.getName());
        assertEquals(original.getId(), cloned.getId());
        assertNotEquals(original.getName(), cloned.getName());
    }

    @Test
    @DisplayName("Debe verificar equals y hashCode correctamente")
    void shouldVerifyEqualsAndHashCode() {
        // Arrange
        Franchise franchise1 = Franchise.builder()
                .id("1")
                .name("Franquicia Test")
                .build();

        Franchise franchise2 = Franchise.builder()
                .id("1")
                .name("Franquicia Test")
                .build();

        Franchise franchise3 = Franchise.builder()
                .id("2")
                .name("Franquicia Test")
                .build();

        // Assert
        assertEquals(franchise1, franchise2);
        assertNotEquals(franchise1, franchise3);
        assertEquals(franchise1.hashCode(), franchise2.hashCode());
    }

    @Test
    @DisplayName("Debe agregar sucursales a la lista")
    void shouldAddBranchesToList() {
        // Arrange
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Franquicia Test")
                .build();

        Branch branch = Branch.builder().name("Nueva Sucursal").build();

        // Act
        franchise.getBranches().add(branch);

        // Assert
        assertEquals(1, franchise.getBranches().size());
        assertEquals("Nueva Sucursal", franchise.getBranches().get(0).getName());
    }
}

