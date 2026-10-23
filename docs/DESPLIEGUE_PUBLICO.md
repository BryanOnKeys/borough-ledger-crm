# Despliegue público — Borough & Ledger CRM

## Estado y topología

- Frontend: Vercel `https://borough-ledger-crm-ashy.vercel.app`, carpeta `frontend`.
- Backend: Render `https://borough-ledger-crm.onrender.com`, carpeta `backend` (Docker).
- MySQL: Aiven, base `defaultdb`, credenciales secretas en Render.
- GitHub `main` activa despliegues automáticos.

`frontend/vercel.json` enruta `/api/:path*` a `https://borough-ledger-crm.onrender.com/api/:path*`, antes de servir `index.html`. Para que la aplicación emplee esta ruta de mismo origen, **no establezcas `VITE_API_URL`** en Vercel.

## Comprobar el servicio

1. Render: abrir `https://borough-ledger-crm.onrender.com/actuator/health`: debe devolver `status: UP`.
2. Vercel: abrir `https://borough-ledger-crm-ashy.vercel.app/api/v1/status`: debe devolver JSON de Spring Boot, no HTML.
3. Vercel: abrir `https://borough-ledger-crm-ashy.vercel.app/api/v1/auth/csrf`: debe devolver JSON con `token` y `headerName` (no publicar el token).
4. Iniciar sesión en la página de Vercel con el **administrador online** creado en Aiven, no el administrador de tu base local.
5. Confirmar clientes y módulos. Los datos de Windows no se copian automáticamente a Aiven.

## Render: variables privadas

- `DB_URL`: URL JDBC **de Aiven**, con `sslMode=REQUIRED` como mínimo. Para producción, validar CA y nombre del servidor, si está disponible.
- `DB_USERNAME`, `DB_PASSWORD`: usuario y contraseña actuales de Aiven.
- `COOKIE_SECURE=true`.
- `CRM_ALLOWED_ORIGINS=https://borough-ledger-crm-ashy.vercel.app`.
- El perfil `bootstrap` debe estar habilitado **solo hasta crear el primer administrador**. Tras confirmar su creación y acceso, eliminar `SPRING_PROFILES_ACTIVE`, `CRM_BOOTSTRAP_EMAIL` y `CRM_BOOTSTRAP_PASSWORD` y guardar/redeploy.
- No publicar los valores privados de `DB_PASSWORD` ni de `CRM_BOOTSTRAP_PASSWORD`; las contraseñas exhibidas en capturas deben rotarse.

## Datos ficticios para Aiven

Las migraciones `backend/src/main/resources/db/migration/V1...V5` se aplican automáticamente con Flyway cuando Spring Boot arranca. Esperar a que `flyway_schema_history` registre V1 a V5 y exista la cuenta ADMIN online.

Para cargar datos **solo en la base remota `defaultdb`** con MySQL Workbench conectado a Aiven, ejecutar en este orden:

1. `sql/05_aiven_datos_operativos.sql`.
2. `sql/06_aiven_clientes_adicionales.sql`.

Ambos archivos seleccionan `defaultdb` y usan inicialmente `admin@local.test`. Si el administrador online usa otro correo, cambiarlo en los scripts **antes de ejecutar**, sin modificar otras identidades ni claves. Los datos son ficticios e idempotentes según el diseño de los scripts. No ejecutar `sql/01_crear_base_y_usuario.sql` contra Aiven.

Comprobar luego:
```sql
USE defaultdb;
SELECT version, success FROM flyway_schema_history ORDER BY installed_rank;
SELECT COUNT(*) AS clientes FROM customers;
SELECT COUNT(*) AS oportunidades FROM opportunities;
SELECT COUNT(*) AS cotizaciones FROM quotations;
SELECT COUNT(*) AS tareas FROM crm_tasks;
```

## Límites y seguridad

Vercel Hobby, Render Free y Aiven Free tienen condiciones, cuotas e interrupciones posibles; no hay garantía de disponibilidad gratuita permanente. Una base persistente no sustituye copias de seguridad. Antes de proporcionar cuentas a terceros, usar una cuenta de prueba con permisos mínimos, aislar datos y revisar autenticación, CSRF, HTTPS y gestión de sesiones. No incluir secretos ni información personal en el repositorio.
