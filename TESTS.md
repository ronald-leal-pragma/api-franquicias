# Tests Unitarios - API Franquicias

## Resumen

Se han implementado **76 tests unitarios** con cobertura completa para todos los casos de uso y modelos del dominio de la aplicación.

## Tecnologías utilizadas

- **JUnit 5** (Jupiter) - Framework de testing
- **Mockito** - Framework de mocking para dependencias
- **Reactor Test** (StepVerifier) - Testing de flujos reactivos (Mono/Flux)
- **AssertJ** / **JUnit Assertions** - Aserciones

## Estructura de tests

### 1. Tests de Casos de Uso (Use Cases)

Ubicación: `src/test/java/com/nequi/franchise/domain/usecase/franchise/`

#### CreateFranchiseUseCaseTest
- ✅ Crear franquicia exitosamente con nombre válido
- ✅ Lanzar error cuando el nombre es nulo
- ✅ Lanzar error cuando el nombre está vacío
- ✅ Lanzar error cuando el nombre solo contiene espacios

#### AddBranchUseCaseTest
- ✅ Agregar sucursal exitosamente con nombre válido
- ✅ Lanzar error cuando el nombre de la sucursal es nulo
- ✅ Lanzar error cuando el nombre está vacío
- ✅ Lanzar error cuando el nombre solo contiene espacios

#### AddProductUseCaseTest
- ✅ Agregar producto exitosamente con stock válido
- ✅ Agregar producto con stock nulo
- ✅ Agregar producto con stock cero
- ✅ Lanzar error cuando el stock es negativo
- ✅ Lanzar error con número negativo grande

#### RemoveProductUseCaseTest
- ✅ Eliminar producto exitosamente
- ✅ Manejar error cuando el gateway falla
- ✅ Invocar gateway con parámetros correctos

#### UpdateStockUseCaseTest
- ✅ Actualizar stock exitosamente con valor positivo
- ✅ Actualizar stock con valor cero
- ✅ Lanzar error cuando el stock es negativo
- ✅ Lanzar error con número negativo grande

#### UpdateProductNameUseCaseTest
- ✅ Actualizar nombre del producto exitosamente
- ✅ Lanzar error cuando el nuevo nombre es nulo
- ✅ Lanzar error cuando el nuevo nombre está vacío
- ✅ Lanzar error cuando el nuevo nombre solo contiene espacios

#### UpdateFranchiseNameUseCaseTest
- ✅ Actualizar nombre de franquicia exitosamente
- ✅ Lanzar error cuando el nuevo nombre es nulo
- ✅ Lanzar error cuando el nuevo nombre está vacío
- ✅ Lanzar error cuando el nuevo nombre solo contiene espacios
- ✅ Invocar gateway con parámetros correctos

#### UpdateBranchNameUseCaseTest
- ✅ Actualizar nombre de sucursal exitosamente
- ✅ Lanzar error cuando el nuevo nombre es nulo
- ✅ Lanzar error cuando el nuevo nombre está vacío
- ✅ Lanzar error cuando el nuevo nombre solo contiene espacios
- ✅ Invocar gateway con parámetros correctos

#### FindMaxStockUseCaseTest
- ✅ Retornar productos con mayor stock por sucursal
- ✅ Retornar Flux vacío cuando no hay resultados
- ✅ Retornar un solo resultado con una sucursal
- ✅ Manejar error cuando el gateway falla
- ✅ Invocar gateway con ID de franquicia correcto

### 2. Tests de Modelos de Dominio

Ubicación: `src/test/java/com/nequi/franchise/domain/model/franchise/`

#### FranchiseTest
- ✅ Crear franquicia con builder exitosamente
- ✅ Crear franquicia con sucursales usando builder
- ✅ Crear franquicia usando constructor sin argumentos
- ✅ Crear franquicia usando constructor con todos los argumentos
- ✅ Modificar propiedades usando setters
- ✅ Clonar franquicia usando toBuilder
- ✅ Verificar equals y hashCode
- ✅ Agregar sucursales a la lista

#### BranchTest
- ✅ Crear sucursal con builder exitosamente
- ✅ Crear sucursal con productos usando builder
- ✅ Crear sucursal usando constructor sin argumentos
- ✅ Crear sucursal usando constructor con todos los argumentos
- ✅ Modificar propiedades usando setters
- ✅ Clonar sucursal usando toBuilder
- ✅ Verificar equals y hashCode
- ✅ Agregar productos a la lista
- ✅ Manejar lista vacía de productos por defecto

#### ProductTest
- ✅ Crear producto con builder exitosamente
- ✅ Crear producto con stock cero
- ✅ Crear producto con stock nulo
- ✅ Crear producto usando constructor sin argumentos
- ✅ Crear producto usando constructor con todos los argumentos
- ✅ Modificar propiedades usando setters
- ✅ Clonar producto usando toBuilder
- ✅ Verificar equals y hashCode
- ✅ Permitir stock con valores grandes
- ✅ Manejar nombres vacíos
- ✅ Crear producto con nombre y sin stock

#### BranchProductResultTest
- ✅ Crear resultado con builder exitosamente
- ✅ Crear resultado usando constructor sin argumentos
- ✅ Crear resultado usando constructor con todos los argumentos
- ✅ Modificar propiedades usando setters
- ✅ Verificar equals y hashCode
- ✅ Permitir producto nulo
- ✅ Permitir nombre de sucursal nulo
- ✅ Manejar nombres de sucursal vacíos
- ✅ Representar correctamente relación sucursal-producto

## Ejecución de tests

### Ejecutar todos los tests del dominio

```bash
./gradlew.bat test --tests "com.nequi.franchise.domain.*"
```

### Ejecutar tests de un caso de uso específico

```bash
./gradlew.bat test --tests "com.nequi.franchise.domain.usecase.franchise.CreateFranchiseUseCaseTest"
```

### Ejecutar tests de modelos

```bash
./gradlew.bat test --tests "com.nequi.franchise.domain.model.franchise.*Test"
```

### Ver reporte HTML de tests

Después de ejecutar los tests, el reporte HTML se genera en:

```
build/reports/tests/test/index.html
```

## Cobertura de código

Los tests cubren:

- **Casos de uso (9 clases)**: Validaciones de negocio, manejo de errores, invocación correcta de gateways
- **Modelos de dominio (4 clases)**: Builders, constructores, setters, equals/hashCode, clonación

### Aspectos cubiertos

✅ **Happy paths** (flujos exitosos)  
✅ **Validaciones de negocio** (nombres vacíos, stock negativo, etc.)  
✅ **Manejo de errores** (IllegalArgumentException, RuntimeException)  
✅ **Edge cases** (valores nulos, vacíos, números grandes)  
✅ **Verificación de mocks** (invocaciones correctas de dependencias)  
✅ **Flujos reactivos** (Mono y Flux con StepVerifier)

## Convenciones utilizadas

- Nombres descriptivos en español con `@DisplayName`
- Estructura AAA (Arrange-Act-Assert)
- Uso de `@ExtendWith(MockitoExtension.class)` para inyección de mocks
- `@BeforeEach` para inicialización común de datos de prueba
- StepVerifier para validar flujos reactivos completos
- Verificación de invocaciones a mocks con `verify()`

## Próximos pasos

- [ ] Añadir tests de integración para adaptadores (MongoDB, REST)
- [ ] Implementar tests de mutación (PIT)
- [ ] Configurar cobertura mínima con JaCoCo
- [ ] Añadir tests de rendimiento para operaciones críticas
- [ ] Tests end-to-end con TestContainers

## Comandos útiles

```bash
# Ejecutar tests con reporte de cobertura (si se configura JaCoCo)
./gradlew.bat test jacocoTestReport

# Ejecutar tests en modo verbose
./gradlew.bat test --info

# Ejecutar tests y continuar ante fallos
./gradlew.bat test --continue

# Limpiar y ejecutar todos los tests
./gradlew.bat clean test
```

---

**Estado actual**: ✅ 76 tests unitarios implementados y pasando exitosamente

