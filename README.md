# Laboratorio 1 - CRUD de Productos y Categorías

## Requisitos
- Docker Desktop corriendo
- Java 25

## Cómo levantar el proyecto
1. En la carpeta `docker` del repositorio, ejecutar:
   `docker compose up -d`
2. Esperar aproximadamente 1 minuto a que Keycloak termine de arrancar. Se puede verificar abriendo http://localhost:8180 en el navegador.
3. Ejecutar la aplicación (`PersistenciaApplication`) desde el IDE. Corre en http://localhost:8080.

Flyway crea las tablas al arrancar. La base de datos inicia sin categorías ni productos.

## Usuarios
Los usuarios y roles están registrados en Keycloak (realm `tienda`), con las contraseñas encriptadas.

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | SUPER-ADMIN-ROLE |
| chris | user123 | USER |

## Cómo usar la colección
1. Ejecutar **Login admin** o **Login usuario**. El token se guarda automáticamente y todos los demás requests lo usan.
2. Ejecutar **Crear categoria** antes de **Crear producto**. Los ids creados se guardan automáticamente en las variables `categoriaId` y `productoId`.
3. El token dura 15 minutos. Si un request responde 401, volver a ejecutar el login.

## Permisos
| Acción | SUPER-ADMIN-ROLE | USER |
|---|---|---|
| Listar y buscar | 200 | 200 |
| Crear | 201 | 403 |
| Actualizar | 200 | 403 |
| Eliminar | 204 | 403 |

Sin token, cualquier request responde 401.