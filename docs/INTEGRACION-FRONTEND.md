# Integración y comprobaciones de BOROUGH & LEDGER

## Orden recomendado

1. **MySQL:** comprobar tablas creadas por Flyway y los datos anteriores.
2. **Backend:** importar ZIP empresarial como proyecto Maven en Eclipse. Ejecutar con las mismas variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y perfil `dev` si es necesario para el primer administrador.
3. **API:** verificar `GET http://127.0.0.1:8080/api/v1/status` y el inicio de sesión con curl o el navegador.
4. **Frontend:** instalar dependencias, iniciar Vite en 127.0.0.1:5173, acceder e iniciar sesión.
5. **Pruebas de operaciones:** registrar cliente, crear contacto, prospecto, embudo, oportunidad, producto, cotización y tarea. Cambiar etapa, consultar informe y descargar PDF. Refrescar la página tras cada guardado para verificar la persistencia.
6. **Permisos:** iniciar sesión con un usuario VIEWER, SALES, MANAGER y ADMIN. Comprobar que la API deniega escrituras no autorizadas, no solo que la interfaz oculta botones.

## Seguridad de sesión y proxy

En desarrollo, Vite tiene proxy de `/api` hacia `http://127.0.0.1:8080`; la URL del navegador se mantiene en `http://127.0.0.1:5173` para evitar problemas de sesión con distintos dominios. Usa siempre la **misma forma de host** (127.0.0.1) y no mezcles `localhost` y `127.0.0.1` al iniciar sesión.

Para producción, el navegador hace solicitudes a `VITE_API_URL`; si es otro sitio distinto de la web, deben configurarse CORS con credenciales, cookies de sesión seguras `SameSite=None; Secure`, protección CSRF y HTTPS. Es preferible un proxy de mismo origen con un dominio propio: este frontend no modifica automáticamente la configuración del backend.

## Validaciones útiles

- `npm run typecheck`: TypeScript.
- `npm run build`: compilación de producción.
- GET `/api/v1/dashboard/summary?currency=PEN`: dashboard conectado.
- `POST /api/v1/auth/login`: devuelve `displayName`, `email`, `role` con cookie de sesión y CSRF válido.
- Para cotización aceptada, confirmar que el servidor impide editar su contenido.
- Consultar informes solo con ADMIN o MANAGER.

## Posibles ajustes de API para crecimiento

- Búsqueda por empresa, contacto, vendedor y etapa con filtros del servidor.
- Endpoint paginado `GET /opportunities?pipelineId=...&stageId=...` para tablero completo (el existente pagina sin esos filtros).
- Endpoints de referencia optimizados que devuelvan nombres relacionados en vez de UUID.
- Vista detallada de actividad, logs, notificaciones y permisos por responsable si son requisitos de producción.
- Pruebas E2E Playwright y CI de frontend tras instalar dependencias en un entorno con red.
