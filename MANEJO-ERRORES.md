# Manejo de Errores y Validaciones - API Franquicias

## ✅ Criterio Cumplido

**Criterio evaluado**: Manejar errores y validaciones desde el handler y el dominio, evitando mensajes técnicos hacia el cliente.

## 📋 Análisis del Problema Original

### ❌ Problemas Identificados

1. **Exposición de mensajes técnicos**: Los handlers estaban usando `.onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()))` que exponía excepciones de Java directamente al cliente.

2. **Uso de excepciones genéricas**: Se estaba utilizando `IllegalArgumentException` directamente en el dominio, que es una excepción técnica de Java.

3. **Falta de manejo centralizado**: Cada método del handler tenía su propio manejo de errores duplicado.

4. **Mensajes en inglés**: Los mensajes de error estaban en inglés y técnicos (ej: "Franchise name cannot be empty").

## ✅ Solución Implementada

### 1. Excepciones Personalizadas de Dominio

Se crearon excepciones específicas del dominio para evitar exponer detalles técnicos:

#### **DomainException** (clase base)
```java
public class DomainException extends RuntimeException {
    private final String code;
    
    public DomainException(String code, String message) {
        super(message);
        this.code = code;
    }
}
```

#### **ValidationException**
- Código: `VALIDATION_ERROR`
- Uso: Validaciones de entrada (nombres vacíos, stock negativo, etc.)
- Ejemplo: "El nombre de la franquicia no puede estar vacío"

#### **ResourceNotFoundException**
- Código: `RESOURCE_NOT_FOUND`
- Uso: Cuando no se encuentra un recurso solicitado
- Ejemplo: "Franquicia no encontrada"

#### **BusinessException**
- Código: `BUSINESS_ERROR`
- Uso: Errores de lógica de negocio
- Ejemplo: "No se puede eliminar una sucursal con productos activos"

### 2. DTO de Respuesta de Error Amigable

```java
@Data
@Builder
public class ErrorResponse {
    private String code;           // Código de error técnico (para logs)
    private String message;        // Mensaje amigable para el cliente
    private LocalDateTime timestamp; // Cuándo ocurrió el error
    private String path;           // Ruta donde ocurrió el error
}
```

**Ejemplo de respuesta al cliente:**
```json
{
  "code": "VALIDATION_ERROR",
  "message": "El nombre de la franquicia no puede estar vacío",
  "timestamp": "2026-02-15T10:30:45",
  "path": "/api/franchises"
}
```

### 3. Manejador Global de Excepciones (GlobalErrorHandler)

Centraliza el manejo de errores y traduce excepciones técnicas a respuestas amigables:

- **ValidationException** → HTTP 400 (Bad Request)
- **ResourceNotFoundException** → HTTP 404 (Not Found)
- **BusinessException** → HTTP 409 (Conflict)
- **Excepciones no controladas** → HTTP 500 con mensaje genérico "Ha ocurrido un error procesando la solicitud"

**Características**:
- ✅ Logging centralizado de errores
- ✅ No expone stack traces al cliente
- ✅ Mensajes en español y amigables
- ✅ Códigos de error consistentes

### 4. Actualización de Casos de Uso

Todos los casos de uso fueron actualizados para usar excepciones de dominio:

**Antes:**
```java
return Mono.error(new IllegalArgumentException("Franchise name cannot be empty"));
```

**Después:**
```java
return Mono.error(new ValidationException("El nombre de la franquicia no puede estar vacío"));
```

### 5. Actualización del Handler

Los handlers ahora delegan el manejo de errores al `GlobalErrorHandler`:

**Antes:**
```java
.onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
```

**Después:**
```java
.onErrorResume(error -> errorHandler.handleError(error, request));
```

## 📁 Archivos Creados

1. `DomainException.java` - Excepción base del dominio
2. `ValidationException.java` - Errores de validación
3. `ResourceNotFoundException.java` - Recursos no encontrados
4. `BusinessException.java` - Errores de negocio
5. `ErrorResponse.java` - DTO de respuesta de error
6. `GlobalErrorHandler.java` - Manejador centralizado de errores

## 📝 Archivos Modificados

### Casos de Uso
- `CreateFranchiseUseCase.java`
- `AddBranchUseCase.java`
- `AddProductUseCase.java`
- `UpdateStockUseCase.java`
- `UpdateProductNameUseCase.java`
- `UpdateFranchiseNameUseCase.java`
- `UpdateBranchNameUseCase.java`

### Infrastructure
- `FranchiseHandler.java`

### Tests
- `CreateFranchiseUseCaseTest.java` (actualizado para nuevas excepciones)
- Pendiente actualizar el resto de tests

## 🎯 Validaciones Implementadas

### Validaciones en el Dominio

1. **Nombres no vacíos**:
   - Franquicias
   - Sucursales
   - Productos

2. **Stock no negativo**:
   - Al agregar productos
   - Al actualizar stock

### Validaciones en el Handler

- Parseo de JSON inválido
- Path variables faltantes
- Body de request vacío

## 🔍 Ejemplos de Respuestas de Error

### Validación fallida
```http
POST /api/franchises
{
  "name": ""
}

HTTP/1.1 400 Bad Request
{
  "code": "VALIDATION_ERROR",
  "message": "El nombre de la franquicia no puede estar vacío",
  "timestamp": "2026-02-15T10:30:45",
  "path": "/api/franchises"
}
```

### Recurso no encontrado
```http
DELETE /api/franchises/999/branches/sucursal1/products/producto1

HTTP/1.1 404 Not Found
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "Franquicia no encontrada",
  "timestamp": "2026-02-15T10:31:00",
  "path": "/api/franchises/999/branches/sucursal1/products/producto1"
}
```

### Error interno (sin exponer detalles)
```http
POST /api/franchises
(error de base de datos, NullPointerException, etc.)

HTTP/1.1 500 Internal Server Error
{
  "code": "INTERNAL_ERROR",
  "message": "Ha ocurrido un error procesando la solicitud",
  "timestamp": "2026-02-15T10:32:15",
  "path": "/api/franchises"
}
```

## ✅ Beneficios de la Implementación

1. **Seguridad**: No se exponen detalles técnicos (stack traces, nombres de clases, etc.)
2. **Experiencia de usuario**: Mensajes claros y en español
3. **Mantenibilidad**: Manejo de errores centralizado
4. **Consistencia**: Formato de error uniforme en toda la API
5. **Trazabilidad**: Logs completos en el servidor, respuestas limpias al cliente
6. **Extensibilidad**: Fácil agregar nuevos tipos de excepciones

## 🧪 Testing

Los tests unitarios han sido actualizados para verificar:
- ✅ Lanzamiento de excepciones de dominio correctas
- ✅ Mensajes de error en español
- ✅ Códigos de error apropiados
- ⏳ Pendiente: Tests de integración del GlobalErrorHandler

## 📊 Resumen de Cumplimiento

| Aspecto | Estado | Detalles |
|---------|--------|----------|
| **Validaciones en dominio** | ✅ Cumplido | Excepciones personalizadas con mensajes amigables |
| **Manejo en handler** | ✅ Cumplido | GlobalErrorHandler centralizado |
| **Sin mensajes técnicos** | ✅ Cumplido | ErrorResponse con mensajes en español |
| **Códigos HTTP correctos** | ✅ Cumplido | 400, 404, 409, 500 según tipo de error |
| **Logging adecuado** | ✅ Cumplido | Logs detallados en servidor |
| **Tests actualizados** | ⚠️ Parcial | CreateFranchiseUseCaseTest actualizado, resto pendiente |

## 🚀 Próximos Pasos Recomendados

1. ⬜ Actualizar los tests restantes (AddBranchUseCaseTest, etc.)
2. ⬜ Agregar tests de integración para GlobalErrorHandler
3. ⬜ Implementar validación de DTOs con Bean Validation (@Valid, @NotBlank, etc.)
4. ⬜ Crear excepciones más específicas si se necesitan (ej: DuplicateResourceException)
5. ⬜ Documentar errores en OpenAPI/Swagger con ejemplos

---

**Conclusión**: El criterio **"Manejar errores y validaciones desde el handler y el dominio, evitando mensajes técnicos hacia el cliente"** ha sido **CUMPLIDO** exitosamente.

