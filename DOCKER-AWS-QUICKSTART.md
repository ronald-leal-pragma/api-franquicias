# Guía Rápida: Docker y AWS ECR

## ⚡ Quick Start

### 1. Build Local

```powershell
# Compilar aplicación
.\gradlew.bat clean build -x test

# Construir imagen Docker
docker build -t api-franquicias:1.0.0 .

# Ejecutar localmente
docker run -p 8080:8080 `
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/franchise_db `
  api-franquicias:1.0.0

# Probar
curl http://localhost:8080/actuator/health
```

---

### 2. Deploy a AWS ECR (Automático)

```powershell
# Configurar variables de entorno
$env:AWS_ACCOUNT_ID = "123456789012"
$env:AWS_REGION = "us-east-1"
$env:ECR_REPOSITORY = "api-franquicias"

# Ejecutar deploy
.\deploy-to-ecr.bat prod 1.0.0
```

---

### 3. Deploy Manual (Paso a Paso)

```powershell
# 1. Compilar
.\gradlew.bat clean build -x test

# 2. Build
docker build -t api-franquicias:1.0.0 .

# 3. Login ECR
aws ecr get-login-password --region us-east-1 | `
  docker login --username AWS --password-stdin `
  123456789012.dkr.ecr.us-east-1.amazonaws.com

# 4. Tag
docker tag api-franquicias:1.0.0 `
  123456789012.dkr.ecr.us-east-1.amazonaws.com/api-franquicias:1.0.0

# 5. Push
docker push 123456789012.dkr.ecr.us-east-1.amazonaws.com/api-franquicias:1.0.0
```

---

## 🚀 Deploy en ECS

### Opción 1: Console AWS

1. Ir a ECS → Task Definitions → Create new
2. Usar el archivo `ecs-task-definition.json`
3. Crear servicio ECS
4. Configurar Load Balancer

### Opción 2: AWS CLI

```powershell
# Registrar task definition
aws ecs register-task-definition `
  --cli-input-json file://ecs-task-definition.json

# Crear servicio
aws ecs create-service `
  --cluster mi-cluster `
  --service-name api-franquicias `
  --task-definition api-franquicias:1 `
  --desired-count 2 `
  --launch-type FARGATE
```

---

## Verificación

### Health Check Local

```powershell
curl http://localhost:8080/actuator/health
curl http://localhost:8080/swagger-ui.html
```

### Health Check en AWS

```powershell
# ECS
aws ecs describe-services `
  --cluster mi-cluster `
  --services api-franquicias

# Logs
aws logs tail /ecs/api-franquicias --follow
```

---

## Comandos Útiles

### Docker

```powershell
# Ver imágenes
docker images | findstr api-franquicias

# Ver contenedores corriendo
docker ps

# Ver logs
docker logs -f <container-id>

# Ejecutar shell en contenedor
docker exec -it <container-id> sh

# Limpiar todo
docker system prune -a
```

### AWS ECR

```powershell
# Listar imágenes
aws ecr list-images --repository-name api-franquicias

# Describir repositorio
aws ecr describe-repositories --repository-names api-franquicias

# Eliminar imagen
aws ecr batch-delete-image `
  --repository-name api-franquicias `
  --image-ids imageTag=old-version
```