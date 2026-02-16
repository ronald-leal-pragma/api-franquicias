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

```text
src/main/java/com/nequi/franchise
├── application                 # Configuración de Beans y Casos de Uso
├── domain                      # Lógica de Negocio (Cero dependencias de Frameworks)
│   ├── model                   # Entidades: Franchise, Branch, Product
│   └── usecase                 # Reglas de negocio: AddBranch, UpdateStock, etc.
└── infrastructure              # Adaptadores de entrada y salida
    ├── driven_adapters         # Persistencia: MongoDB Repository & Adapter
    └── entry_points            # API REST: RouterFunction & Handlers
```

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

## Dockerización y Despliegue en AWS

### Construcción de Imagen Docker

#### Dockerfile Simple (Recomendado para desarrollo)

```powershell
# 1. Compilar la aplicación
.\gradlew.bat clean build -x test

# 2. Construir imagen Docker
docker build -t api-franquicias:1.0.0 .

# 3. Ejecutar contenedor
docker run -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/franchise_db \
  api-franquicias:1.0.0
```

---

## ☁️ Despliegue en AWS ECR (Elastic Container Registry)

### Prerrequisitos

1. **AWS CLI instalado y configurado**:
   ```powershell
   # Instalar AWS CLI
   winget install Amazon.AWSCLI
   
   # Configurar credenciales
   aws configure
   ```

2. **Variables de entorno necesarias**:
   ```powershell
   # ID de tu cuenta AWS (12 dígitos)
   $env:AWS_ACCOUNT_ID = "123456789012"
   
   # Región de AWS
   $env:AWS_REGION = "us-east-1"
   
   # Nombre del repositorio ECR
   $env:ECR_REPOSITORY = "api-franquicias"
   ```

### Despliegue Manual Paso a Paso

Si prefieres hacerlo manualmente:

```powershell
# 1. Compilar la aplicación
.\gradlew.bat clean build -x test

# 2. Construir imagen Docker
docker build -t api-franquicias:1.0.0 .

# 3. Login a AWS ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789012.dkr.ecr.us-east-1.amazonaws.com

# 4. Crear repositorio ECR (solo la primera vez)
aws ecr create-repository `
  --repository-name api-franquicias `
  --region us-east-1 `
  --image-scanning-configuration scanOnPush=true `
  --encryption-configuration encryptionType=AES256

# 5. Etiquetar imagen para ECR
$ECR_URI = "123456789012.dkr.ecr.us-east-1.amazonaws.com/api-franquicias"
docker tag api-franquicias:1.0.0 "$ECR_URI:1.0.0"
docker tag api-franquicias:1.0.0 "$ECR_URI:latest"

# 6. Subir imagen a ECR
docker push "$ECR_URI:1.0.0"
docker push "$ECR_URI:latest"
```

## 🚀 Despliegue en Servicios AWS

### AWS ECS (Elastic Container Service)

#### Comandos ECS:

```powershell
# Registrar task definition
aws ecs register-task-definition --cli-input-json file://task-definition.json

# Crear servicio ECS
aws ecs create-service `
  --cluster mi-cluster `
  --service-name api-franquicias `
  --task-definition api-franquicias:1 `
  --desired-count 2 `
  --launch-type FARGATE `
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx],securityGroups=[sg-xxx],assignPublicIp=ENABLED}"

# Actualizar servicio con nueva imagen
aws ecs update-service `
  --cluster mi-cluster `
  --service api-franquicias `
  --force-new-deployment
```