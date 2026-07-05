# API de Gestión de Pólizas

API REST construida con **Spring Boot 3 / Java 17** para la gestión de pólizas y sus riesgos asociados. Implementa una arquitectura por capas (`controller` → `service` → `repository` → `entity`), validaciones de negocio y una capa de seguridad mínima basada en API Key.

## Tecnologías utilizadas

- Java 17
- Spring Boot 3.2
- Spring Data JPA
- Spring Validation
- H2 Database (en memoria)
- Maven
- Lombok

## Arquitectura

El proyecto sigue una arquitectura por capas:

```
Controller  →  Service  →  Repository  →  Base de datos
```

- **Controller**: expone los endpoints REST y delega toda la lógica a la capa Service, sin validaciones de negocio en este nivel.
- **Service**: concentra las reglas de negocio (renovación, cancelación en cascada, validación de tipo de póliza) y la orquestación transaccional (`@Transactional`).
- **Repository**: acceso a datos vía Spring Data JPA, sin lógica adicional.
- **Entity**: modelo de dominio (`Poliza`, `Riesgo`) con sus relaciones y enums de estado.

Además, la integración con el CORE legado se aísla en un componente de tipo *mock/adapter* (`CoreMockClient`), y los errores de negocio se centralizan en un `GlobalExceptionHandler`, evitando manejo de excepciones disperso en los controllers.

## 1. Requisitos

- Java 17+
- Maven 3.8+
- (Opcional) Postman / cURL para probar los endpoints

## 2. Ejecución del proyecto

```bash
# Compilar y correr tests
mvn clean install

# Levantar la aplicación
mvn spring-boot:run
```

La aplicación arranca en `http://localhost:8080` y usa una base de datos **H2 en memoria** (se recarga en cada arranque con datos de prueba definidos en `src/main/resources/data.sql`).

Consola H2 disponible en: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:polizasdb`
- Usuario: `sa` / Password: *(vacío)*

## 3. Seguridad

Todos los endpoints (excepto la consola H2) requieren el siguiente header en cada petición:

```
x-api-key: 123456
```

Si el header falta o es incorrecto, la API responde `401 Unauthorized`.

## 4. Datos de prueba precargados

| id | numeroPoliza  | tipo       | estado    | canon | prima  |
|----|---------------|------------|-----------|-------|--------|
| 1  | POL-IND-001   | INDIVIDUAL | ACTIVA    | 100.0 | 500.0  |
| 2  | POL-COL-001   | COLECTIVA  | ACTIVA    | 300.0 | 1500.0 |
| 3  | POL-IND-002   | INDIVIDUAL | CANCELADA | 120.0 | 600.0  |

Riesgos: la póliza 1 tiene 1 riesgo (individual), la póliza 2 tiene 2 riesgos (colectiva).

## 5. Endpoints

Todos los ejemplos incluyen el header obligatorio `-H "x-api-key: 123456"`.

### 5.1 Listar pólizas (con filtros opcionales)
```bash
curl -H "x-api-key: 123456" "http://localhost:8080/polizas?tipo=COLECTIVA&estado=ACTIVA"
```
Parámetros query opcionales: `tipo` (`INDIVIDUAL` | `COLECTIVA`), `estado` (`ACTIVA` | `RENOVADA` | `CANCELADA`).

### 5.2 Listar riesgos de una póliza
```bash
curl -H "x-api-key: 123456" "http://localhost:8080/polizas/2/riesgos"
```

### 5.3 Renovar una póliza
Incrementa `canon` y `prima` en +IPC (configurable en `application.yml`, por defecto 5%) y cambia el estado a `RENOVADA`. No permitido si la póliza está `CANCELADA`.
```bash
curl -X POST -H "x-api-key: 123456" "http://localhost:8080/polizas/1/renovar"
```

### 5.4 Cancelar una póliza
Cambia el estado a `CANCELADA` y cancela en cascada todos sus riesgos asociados.
```bash
curl -X POST -H "x-api-key: 123456" "http://localhost:8080/polizas/2/cancelar"
```

### 5.5 Agregar un riesgo a una póliza
Solo permitido si la póliza es de tipo `COLECTIVA` y no está cancelada.
```bash
curl -X POST -H "x-api-key: 123456" -H "Content-Type: application/json" \
  -d '{"descripcion":"Nuevo riesgo empleado C"}' \
  "http://localhost:8080/polizas/2/riesgos"
```

### 5.6 Cancelar un riesgo puntual
```bash
curl -X POST -H "x-api-key: 123456" "http://localhost:8080/riesgos/2/cancelar"
```

### 5.7 Mock del CORE
Endpoint que simula la recepción de un evento externo y únicamente lo registra en logs.
```bash
curl -X POST -H "x-api-key: 123456" -H "Content-Type: application/json" \
  -d '{"evento":"ACTUALIZACION","polizaId":555}' \
  "http://localhost:8080/core-mock/evento"
```

## 6. Reglas de negocio implementadas

| Regla | Dónde se valida |
|---|---|
| Una póliza `INDIVIDUAL` solo puede tener 1 riesgo | Garantizado por construcción: `PolizaService.agregarRiesgo` solo permite agregar riesgos a pólizas `COLECTIVA`, por lo que una `INDIVIDUAL` nunca puede superar su riesgo inicial vía API |
| No se puede renovar una póliza `CANCELADA` | `PolizaService.renovar` |
| Cancelar una póliza cancela en cascada sus riesgos | `PolizaService.cancelar` |
| Agregar un riesgo exige que la póliza sea `COLECTIVA` y no esté cancelada | `PolizaService.agregarRiesgo` |
| No se puede cancelar dos veces la misma póliza/riesgo | `PolizaService.cancelar` / `RiesgoService.cancelar` |

Las violaciones de estas reglas devuelven `400 Bad Request` con un mensaje descriptivo, gracias al `GlobalExceptionHandler`.

## 7. Estructura del proyecto

```
polizas-api/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/polizas/
    │   ├── PolizasApiApplication.java
    │   ├── config/
    │   │   └── ApiKeyFilter.java
    │   ├── controller/
    │   │   ├── PolizaController.java
    │   │   ├── RiesgoController.java
    │   │   └── CoreMockController.java
    │   ├── dto/
    │   │   ├── RiesgoRequest.java
    │   │   └── CoreEventoRequest.java
    │   ├── entity/
    │   │   ├── Poliza.java
    │   │   ├── Riesgo.java
    │   │   ├── TipoPoliza.java
    │   │   ├── EstadoPoliza.java
    │   │   └── EstadoRiesgo.java
    │   ├── exception/
    │   │   ├── BusinessException.java
    │   │   ├── ResourceNotFoundException.java
    │   │   └── GlobalExceptionHandler.java
    │   ├── mock/
    │   │   └── CoreMockClient.java
    │   ├── repository/
    │   │   ├── PolizaRepository.java
    │   │   └── RiesgoRepository.java
    │   └── service/
    │       ├── PolizaService.java
    │       └── RiesgoService.java
    └── resources/
        ├── application.yml
        └── data.sql
```

## 8. Notas de diseño

- **IPC configurable**: el porcentaje de incremento en renovación está externalizado en `application.yml` (`app.business.ipc`), evitando "números mágicos" en el código.
- **Cascade en JPA**: `Poliza.riesgos` usa `CascadeType.ALL` y `orphanRemoval = true`, lo que simplifica la cancelación en cascada de riesgos.
- **Manejo centralizado de errores**: `GlobalExceptionHandler` estandariza las respuestas de error (timestamp, status, error, message).
- **Filtro de seguridad**: implementado con `OncePerRequestFilter`, ligero y sin acoplar Spring Security completo, adecuado para el alcance de la prueba.
