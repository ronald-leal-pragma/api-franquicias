# API Franquicias

![Java](https://img.shields.io/badge/Java-21-blue?style=flat&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-6DB33F?style=flat&logo=spring)
![Gradle](https://img.shields.io/badge/Gradle-8.14.4-02303A?style=flat&logo=gradle)
![MongoDB](https://img.shields.io/badge/MongoDB-not%20specified-green?style=flat&logo=mongodb)

Descripción
-----------
Proyecto base para una API reactiva que gestiona una red de franquicias. Contiene la estructura inicial del proyecto, configuración de Gradle y clases esqueleto.

Stack principal
---------------
- Java 21 (Toolchain en `build.gradle`)
- Spring Boot 3.4.1 (WebFlux, Actuator, Validation)
- Gradle 8.14.4 (wrapper incluido)
- MongoDB (reactive, versión no fijada en dependencias)
- MapStruct, Lombok y Springdoc OpenAPI

Estructura del proyecto
-----------------------
Puntos clave de la estructura de carpetas y archivos:

- `build.gradle`, `settings.gradle.kts`, `gradlew` / `gradlew.bat` - configuración y wrapper de Gradle.
- `src/main/java` - código fuente de la aplicación:
  - `application/config` - clase principal y configuración de Spring Boot.
  - `domain/model` - modelos de dominio (`user`, `gate`, etc.).
  - `domain/usecase` - casos de uso de la lógica de negocio.
  - `infrastructure/driven_adapters` - adaptadores para persistencia (por ejemplo MongoDB).
  - `entrypoints/reactiveweb` - controladores/routers y adaptadores de entrada WebFlux.
- `src/main/resources` - recursos y `application.properties`.
- `src/test` - pruebas unitarias / de integración.

Quick start (Windows / PowerShell)
---------------------------------
Requisitos:
- JDK 21
- Docker (recomendado para arrancar MongoDB localmente) o una instancia de MongoDB accesible.

1) Arrancar MongoDB con Docker (opcional)

Puedes usar Docker Compose o arrancar un contenedor simple; ejemplo de `docker-compose.yml` mínimo:

```yaml
version: '3.8'
services:
  mongodb:
    image: mongo:6.0
    container_name: api-franquicias-mongodb
    ports:
      - 27017:27017
    volumes:
      - mongo-data:/data/db
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: example

volumes:
  mongo-data:
```

Arrancar con Docker Compose:

```powershell
docker compose up -d
```

O con `docker run`:

```powershell
docker run -d --name api-franquicias-mongo -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=example mongo:6.0
```

2) Configurar la conexión a MongoDB

Por defecto la aplicación no contiene credenciales en `application.properties`. Puedes especificar la URI de conexión mediante la variable de entorno `SPRING_DATA_MONGODB_URI` o editando `src/main/resources/application.properties` con una línea como:

```
spring.data.mongodb.uri=mongodb://root:example@localhost:27017/?authSource=admin
```

3) Ejecutar la aplicación (Windows / PowerShell)

```powershell
# Ejecutar la aplicación
.\gradlew.bat bootRun

# Compilar y ejecutar tests
.\gradlew.bat clean build

# Ejecutar tests
.\gradlew.bat test
```

Notas de desarrollo
-------------------
- Usa ramas `feature/` para nuevas funcionalidades (por ejemplo `feature/user-domain`).
- Añade configuraciones específicas de entorno en `application-{profile}.properties` y no subas credenciales.

Variables de entorno recomendadas
--------------------------------
- `SPRING_DATA_MONGODB_URI` - URI de conexión a MongoDB.
- `SPRING_PROFILES_ACTIVE` - perfil de Spring (dev/test/prod).

Comandos útiles (Git)
---------------------
```powershell
# Crear rama feature
git checkout -b feature/nombre-de-la-caracteristica

# Subir rama al remoto
git push -u origin feature/nombre-de-la-caracteristica
```

FAQ — ejecución local
---------------------
- ¿Qué pasa si no tengo Docker? Puedes apuntar la aplicación a un MongoDB remoto o local ya instalado modificando `spring.data.mongodb.uri`.
- ¿Qué versión de MongoDB usar? Se recomienda usar la 6.x o la versión que el equipo defina; actualmente no está fijada en `build.gradle`.

