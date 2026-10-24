-- BOROUGH & LEDGER CRM | Activar acceso público de evaluación (solo lectura)
-- EJECUTAR SOLO en la conexión CRM Aiven Online (defaultdb), si Render no ha aplicado V6.
-- La cuenta se asocia al administrador de prueba y a 20 clientes ficticios con email .example.
-- Repetir este script no duplica usuarios ni modifica contraseñas de cuentas existentes.
-- La contraseña de esta cuenta es PUBLICA en el Post-it. Nunca usarla para cuentas privadas.
USE defaultdb;

INSERT INTO app_users
  (id, organization_id, email, password_hash, display_name, role, enabled, created_at)
SELECT UUID(),
       a.organization_id,
       'demo@local.test',
       '$2y$12$pWKPjmjEqaUtkX/YjcIjX.8/e6JN/FlqLRTAYPpRHHYHXZQ.TdAgG',
       'Invitado de evaluación',
       'VIEWER',
       TRUE,
       UTC_TIMESTAMP(6)
FROM app_users AS a
WHERE a.email = 'admin@local.test'
  AND a.role = 'ADMIN'
  AND a.enabled = TRUE
  AND (SELECT COUNT(*) FROM customers AS c
       WHERE c.organization_id = a.organization_id
         AND c.archived_at IS NULL) = 20
  AND (SELECT COUNT(*) FROM customers AS c
       WHERE c.organization_id = a.organization_id
         AND c.archived_at IS NULL
         AND c.email LIKE '%.example') = 20
LIMIT 1
ON DUPLICATE KEY UPDATE email = email;

-- Confirma que se creó una cuenta activa, con rol VIEWER y los datos de prueba:
SELECT u.email, u.display_name, u.role, u.enabled,
       (SELECT COUNT(*) FROM customers c
        WHERE c.organization_id = u.organization_id AND c.archived_at IS NULL) AS clientes_accesibles
FROM app_users u
WHERE u.email = 'demo@local.test';
