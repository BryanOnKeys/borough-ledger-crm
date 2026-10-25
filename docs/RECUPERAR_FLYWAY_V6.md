# Recuperar despliegue fallido por Flyway V6 (MySQL/Aiven)

Error de Render: `Error Code 1052: Column 'email' in field list is ambiguous` en `V6__demo_readonly_user.sql`.

## Causa y corrección

El `INSERT ... SELECT ... ON DUPLICATE KEY UPDATE email = email` usaba `email` sin calificar: MySQL no sabía si referirse a `app_users` destino o al origen del `SELECT`.

Se reemplazó esa expresión por un valor literal sin ambigüedad y se añadió una prueba de integración con MySQL 8.4 y un usuario VIEWER ya existente. El cambio **no modifica** el correo del administrador, sus credenciales ni datos comerciales.

## Recuperación en Aiven (solo si existe la marca de fracaso)

1. Crear una copia de seguridad o exportación de `defaultdb` antes de modificar migraciones.
2. Abrir Workbench en **CRM Aiven Online**, NO en la conexión local, y ejecutar:

```sql
USE defaultdb;
SELECT DATABASE() AS base_actual;
SELECT installed_rank, version, description, script, success
FROM flyway_schema_history
WHERE version = '6';
```

3. Si no hay ninguna fila: no modificar `flyway_schema_history`; en Render desplegar el último commit de `main`.
4. Si aparece una fila para V6 con `success = 0` y `script = 'V6__demo_readonly_user.sql'`, el fallo ocurrió antes de aplicar completamente la migración. Confirmar que la cuenta VIEWER ya existe o que los datos están correctos, y **solo entonces** limpiar el registro fallido:

```sql
USE defaultdb;
SELECT email, role, enabled FROM app_users WHERE email='demo@local.test';
DELETE FROM flyway_schema_history
WHERE version='6'
  AND success=0
  AND script='V6__demo_readonly_user.sql';
SELECT ROW_COUNT() AS registros_fallidos_eliminados;
```

**Advertencia:** este cambio toca únicamente metadatos de una migración fallida. Es equivalente a retirar el registro fallido mediante `flyway repair`, pero es preferible ejecutar el comando oficial si hay una sesión de Flyway disponible. No eliminar filas de V1–V5 ni filas de V6 con `success=1`. Nunca ejecutar `DROP DATABASE`, `flyway clean`, ni borrar datos comerciales para resolver este problema.

5. En Render, **Manual Deploy → Deploy latest commit**. Confirmar `Live`, verificar `/actuator/health` con `status=UP` y realizar acceso de invitado desde Vercel.
6. Verificar que V6 aparece con `success=1` en `flyway_schema_history`. Si existe V6 con `success=1` pero una suma de verificación diferente a la nueva versión, utilizar `flyway repair` para realinear el checksum, no eliminar una migración exitosa de forma manual.

Si `DELETE` afecta más de una fila o hay un estado diferente al descrito, no continuar y revisar registros antes de tocar el historial.
