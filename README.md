# Microservicio de Autentificación

## server.port=8094

## Endpoints

### POST `/api/v1/autentificacion/login`
Verifica las credenciales del usuario contra el MS Usuario.

**JSON de entrada:**
```json
{
    "correo": "daniela.picarte@gmail.com",
    "password": "123456A"
}
```

**Respuestas posibles:**
| Situación | Status | Mensaje |
|---|---|---|
| Login exitoso | 200 OK | `Bienvenido Daniela Picarte, su rol es: ADMIN` |
| Credenciales incorrectas | 200 OK | `Credenciales incorrectas` |
| MS Usuario caído | 409 CONFLICT | `Servicio de autentificacion no disponible, intente mas tarde` |

---

### GET `/api/v1/autentificacion`
Lista todos los registros de login almacenados.

**Respuesta:**
```json
[
    {
        "idAutentificacion": 1,
        "correo": "daniela.picarte@gmail.com",
        "password": "123456A",
        "fechaLogin": "2026-05-25",
        "idUsuario": 1
    }
]
```

---

## Dependencias
| MS | Puerto | Para qué |
|---|---|---|
| MS Usuario | 8083 | Verificar credenciales del usuario |