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

Quick start (Windows / PowerShell)
---------------------------------
Requisitos:
- JDK 21
- Docker o una instancia de MongoDB disponible (opcional para desarrollo)

Ejecutar la aplicación usando el wrapper de Gradle:

```powershell
# Ejecutar la aplicación
.\gradlew.bat bootRun

# Compilar y ejecutar tests
.\gradlew.bat clean build

# Ejecutar tests
.\gradlew.bat test
```

Comandos útiles (Git)
---------------------
```powershell
# Crear rama feature
git checkout -b feature/nombre-de-la-caracteristica

# Subir rama al remoto
git push -u origin feature/nombre-de-la-caracteristica
```

Notas
-----
- No se incluyen binarios ni artefactos compilados en el repositorio (.gitignore configurado para excluir `build/`, `.gradle/`, `.idea/`, `*.class`).
- La versión de MongoDB no está fijada en `build.gradle`; recomienda especificarla en la documentación del equipo o en `README` cuando se decida.

Si quieres, puedo:
- Añadir ejemplos de configuración de MongoDB (URI, docker-compose).
- Añadir un `CONTRIBUTING.md` y plantilla para `PR`.

