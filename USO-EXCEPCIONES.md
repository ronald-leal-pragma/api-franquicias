# Uso de Excepciones de Dominio

## 📋 Pregunta Original

> ¿Qué función tienen las clases `ResourceNotFoundException` y `BusinessException`? Sus métodos no son usados.

## ✅ Respuesta

Estas clases **SÍ tienen función importante** y ahora **están siendo utilizadas** correctamente en el proyecto.

---

## 🎯 Propósito de Cada Excepción

### 1. **ValidationException** 
- **HTTP**: 400 Bad Request
- **Cuándo usarla**: Errores en la entrada del usuario
- **Ejemplos**:
  - Nombre vacío o nulo
  - Stock negativo
  - Formato inválido

**Uso actual:**
```java
if (franchise.getName() == null || franchise.getName().trim().isEmpty()) {
    return Mono.error(new ValidationException("El nombre de la franquicia no puede estar vacío"));
}
```

---

### 2. **ResourceNotFoundException** ✅ AHORA EN USO
- **HTTP**: 404 Not Found
- **Cuándo usarla**: Cuando un recurso solicitado no existe en la base de datos
- **Ejemplos**:
  - Franquicia no encontrada por ID
  - Sucursal no encontrada
  - Producto no encontrado

**Implementación realizada:**

#### En `FranchiseRepositoryAdapter.java`:
```java
// Antes (❌ mal):
.switchIfEmpty(Mono.error(new RuntimeException("Franchise not found")))

// Ahora (✅ correcto):
.switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada con ID: " + franchiseId)))
```

**Métodos actualizados:**
- `addBranch()` - Franquicia no encontrada
- `addProduct()` - Franquicia o sucursal no encontrada
- `removeProduct()` - Franquicia o sucursal no encontrada
- `updateStock()` - Producto no encontrado
- `updateFranchiseName()` - Franquicia no encontrada
- `updateBranchName()` - Franquicia o sucursal no encontrada
- `updateProductName()` - Producto no encontrado

---

### 3. **BusinessException** ✅ AHORA EN USO
- **HTTP**: 409 Conflict
- **Cuándo usarla**: Violación de reglas de negocio (no de validación técnica)
- **Ejemplos**:
  - Duplicados no permitidos
  - Estado inconsistente
  - Operación no permitida por regla de negocio

**Implementación realizada:**

#### En `AddBranchUseCase.java`:
```java
public Mono<Franchise> apply(String franchiseId, Branch branch) {
    // 1. Validación técnica
    if (branch.getName() == null || branch.getName().isBlank()) {
        return Mono.error(new ValidationException("El nombre de la sucursal no puede estar vacío"));
    }

    // 2. Validación de negocio
    return gateway.findById(franchiseId)
            .flatMap(franchise -> {
                boolean branchExists = franchise.getBranches().stream()
                        .anyMatch(b -> b.getName().equalsIgnoreCase(branch.getName()));
                
                if (branchExists) {
                    return Mono.error(new BusinessException(
                        "Ya existe una sucursal con el nombre '" + branch.getName() + "' en esta franquicia"
                    ));
                }
                
                return gateway.addBranch(franchiseId, branch);
            });
}
```

---

## 📊 Diferencias Clave

| Excepción | HTTP | Cuándo | Ejemplo |
|-----------|------|--------|---------|
| **ValidationException** | 400 | Entrada inválida | "El nombre no puede estar vacío" |
| **ResourceNotFoundException** | 404 | Recurso no existe | "Franquicia no encontrada con ID: 123" |
| **BusinessException** | 409 | Regla de negocio violada | "Ya existe una sucursal con ese nombre" |

---

## 🔍 Ejemplo Completo de Flujo

### Escenario: Agregar una sucursal

#### Request:
```http
POST /api/franchises/123/branches
Content-Type: application/json

{
  "name": "Sucursal Norte"
}
```

#### Posibles Respuestas:

**1. ValidationException (400) - Nombre vacío:**
```json
{
  "code": "VALIDATION_ERROR",
  "message": "El nombre de la sucursal no puede estar vacío",
  "timestamp": "2026-02-15T10:30:00",
  "path": "/api/franchises/123/branches"
}
```

**2. ResourceNotFoundException (404) - Franquicia no existe:**
```json
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "Franquicia no encontrada con ID: 123",
  "timestamp": "2026-02-15T10:30:00",
  "path": "/api/franchises/123/branches"
}
```

**3. BusinessException (409) - Sucursal duplicada:**
```json
{
  "code": "BUSINESS_ERROR",
  "message": "Ya existe una sucursal con el nombre 'Sucursal Norte' en esta franquicia",
  "timestamp": "2026-02-15T10:30:00",
  "path": "/api/franchises/123/branches"
}
```

**4. Success (200) - Todo OK:**
```json
{
  "id": "123",
  "name": "Mi Franquicia",
  "branches": [
    {
      "name": "Sucursal Norte",
      "products": []
    }
  ]
}
```

---

## 📁 Archivos Modificados

### 1. **FranchiseRepositoryAdapter.java**
- ✅ Agregado import de `ResourceNotFoundException`
- ✅ Reemplazadas 7 instancias de `RuntimeException` por `ResourceNotFoundException`
- ✅ Mensajes de error en español

### 2. **AddBranchUseCase.java**
- ✅ Agregado import de `BusinessException`
- ✅ Nueva validación de negocio para detectar sucursales duplicadas
- ✅ Lanzamiento de `BusinessException` cuando corresponde

### 3. **AddBranchUseCaseTest.java**
- ✅ Agregado import de `BusinessException`
- ✅ Actualizados tests existentes para mockear `findById()`
- ✅ Nuevo test: `shouldThrowBusinessExceptionWhenBranchAlreadyExists()`

---

## 🧪 Tests Agregados

### Nuevo Test:
```java
@Test
@DisplayName("Debe lanzar error de negocio cuando ya existe una sucursal con el mismo nombre")
void shouldThrowBusinessExceptionWhenBranchAlreadyExists() {
    // Arrange
    Branch existingBranch = Branch.builder().name("Sucursal Centro").build();
    franchise.getBranches().add(existingBranch);
    
    when(gateway.findById(eq(franchiseId)))
            .thenReturn(Mono.just(franchise));

    // Act
    Mono<Franchise> result = addBranchUseCase.apply(franchiseId, branch);

    // Assert
    StepVerifier.create(result)
            .expectErrorMatches(throwable ->
                    throwable instanceof BusinessException &&
                            throwable.getMessage().contains("Ya existe una sucursal con el nombre"))
            .verify();
}
```

---

## ✅ Beneficios de la Implementación

### Antes (❌):
```java
.switchIfEmpty(Mono.error(new RuntimeException("Franchise not found")))
```
- Mensaje en inglés
- Excepción genérica
- No hay código de error específico
- HTTP 500 (Internal Server Error) - incorrecto

### Ahora (✅):
```java
.switchIfEmpty(Mono.error(new ResourceNotFoundException("Franquicia no encontrada con ID: " + franchiseId)))
```
- Mensaje en español
- Excepción específica del dominio
- Código de error: `RESOURCE_NOT_FOUND`
- HTTP 404 (Not Found) - correcto semánticamente

---

## 🎯 Casos de Uso Futuros

### Más ejemplos de BusinessException:

1. **Stock máximo excedido:**
```java
if (product.getStock() > MAX_STOCK_PER_PRODUCT) {
    return Mono.error(new BusinessException(
        "El stock no puede exceder " + MAX_STOCK_PER_PRODUCT + " unidades"
    ));
}
```

2. **Límite de sucursales:**
```java
if (franchise.getBranches().size() >= MAX_BRANCHES) {
    return Mono.error(new BusinessException(
        "Una franquicia no puede tener más de " + MAX_BRANCHES + " sucursales"
    ));
}
```

3. **Eliminar sucursal con productos:**
```java
if (!branch.getProducts().isEmpty()) {
    return Mono.error(new BusinessException(
        "No se puede eliminar una sucursal que tiene productos activos"
    ));
}
```

---

## 📊 Resumen de Cambios

| Archivo | Cambios | Líneas |
|---------|---------|--------|
| `FranchiseRepositoryAdapter.java` | +1 import, 7 RuntimeException → ResourceNotFoundException | ~15 |
| `AddBranchUseCase.java` | +1 import, +validación de negocio | ~10 |
| `AddBranchUseCaseTest.java` | +1 import, actualización de tests, +1 test | ~20 |

**Total**: 3 archivos modificados, ~45 líneas cambiadas

---

## ✅ Conclusión

### Las clases `ResourceNotFoundException` y `BusinessException`:

1. ✅ **SÍ tienen función importante** en la arquitectura
2. ✅ **AHORA están siendo utilizadas** correctamente
3. ✅ **Mejoran la semántica** de los errores HTTP
4. ✅ **Proporcionan mensajes claros** al cliente
5. ✅ **Separan responsabilidades** (validación vs. negocio vs. recursos)

### No deben eliminarse porque:

- Son parte del diseño de excepciones de dominio
- Permiten manejar diferentes escenarios de error
- El `GlobalErrorHandler` las traduce a códigos HTTP correctos
- Facilitan el testing y la mantenibilidad
- Siguen el principio de responsabilidad única

---

**Estado**: ✅ Excepciones implementadas y funcionando correctamente

