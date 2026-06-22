# BookPoint · Microservicio de Autentificación (`ms-autentificacion`)

Microservicio encargado de la autentificación de usuarios dentro del sistema **BookPoint**. Expone una API REST con documentación Swagger y se comunica con el microservicio de usuarios para verificar credenciales y registrar los accesos al sistema.

---

## 🛠️ Tecnologías

- **Java 24**
- **Spring Boot 4.0.6**
- **Spring Web MVC**
- **Spring Data JPA**
- **MySQL** (entorno de producción/desarrollo)
- **H2** (base de datos en memoria para tests)
- **Lombok** — reducción de código boilerplate
- **Jackson** — serialización/deserialización JSON
- **Spring Boot Actuator** — monitoreo del microservicio
- **SpringDoc OpenAPI / Swagger** — documentación de la API
- **RestTemplate** — comunicación con otros microservicios
- **JUnit 5 + Mockito** — pruebas unitarias e integración

---

## 🏗️ Rol en la arquitectura

`ms-autentificacion` consulta al microservicio de usuarios para verificar las credenciales al momento del login.

| Microservicio | Para qué se consulta |
|---------------|----------------------|
| `ms-usuario` | Verificar credenciales del usuario (correo y password) |

```
Cliente → Gateway (8080) → ms-autentificacion (8094)
                                │
                                └── RestTemplate → ms-usuario (8083)
```

---

## ✅ Requisitos previos

- JDK 24 o superior
- Maven 3.8+
- MySQL en ejecución (para el perfil por defecto)
- MS Usuario corriendo si vas a probar el flujo completo

---

## ⚙️ Configuración

### `src/main/resources/application.properties`

```properties
spring.application.name=autentificacion
server.port=8094

spring.datasource.url=jdbc:mysql://localhost:3306/autentificaciondb
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/doc/swagger-ui.html
```

### `src/test/resources/application-test.properties`

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql=TRACE
```

---

## 🌐 Acceso vía API Gateway

```
POST http://localhost:8080/api/v1/autentificacion/login
```

Rutas configuradas en el gateway:

```yaml
- id: ms-autentificacion
  uri: http://localhost:8094
  predicates:
    - Path=/api/v1/autentificacion,/api/v1/autentificacion/**
```

---

## 📡 Endpoints

### Autentificación — Base: `/api/v1/autentificacion`

| Método | Endpoint | Descripción | Código éxito |
|--------|----------|-------------|--------------|
| `POST` | `/api/v1/autentificacion/login` | Iniciar sesión | `200 OK` |
| `GET` | `/api/v1/autentificacion` | Listar historial de logins | `200 OK` |

### Respuestas posibles del login

| Situación | Status | Mensaje |
|-----------|--------|---------|
| Login exitoso | `200 OK` | `Bienvenido {nombre} {apellido}, su rol es: {rol}` |
| Credenciales incorrectas | `200 OK` | `Credenciales incorrectas` |
| MS Usuario caído | `409 CONFLICT` | `Servicio de autentificacion no disponible, intente mas tarde` |

### JSON de entrada para POST login

```json
{
  "correo": "daniela.picarte@gmail.com",
  "password": "123456A"
}
```

---

## 🗂️ Modelos de datos

### `Autentificacion`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idAutentificacion` | `Long` | Identificador único (PK) |
| `correo` | `String` | Correo del usuario |
| `password` | `String` | Contraseña del usuario |
| `fechaLogin` | `LocalDateTime` | Fecha y hora del inicio de sesión |
| `idUsuario` | `Long` | ID del usuario autentificado |

---

- **Pruebas unitarias:** `@ExtendWith(MockitoExtension.class)` con `@Mock` / `@InjectMocks`.
- **Pruebas de integración:** `@SpringBootTest` + `@AutoConfigureMockMvc` + `@ActiveProfiles("test")`.
- Las llamadas a otros microservicios (`RestTemplate`) se mockean con Mockito.

---

## 📁 Estructura del proyecto

```
ms-autentificacion/
├── src/
│   ├── main/
│   │   ├── java/BookPoint/autentificacion/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── AutentificacionApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/BookPoint/autentificacion/
│       │   ├── controller/
│       │   └── service/
│       └── resources/
│           └── application-test.properties
└── pom.xml
```

---

## 👤 Autor

Proyecto **BookPoint** — Microservicio de Autentificación.