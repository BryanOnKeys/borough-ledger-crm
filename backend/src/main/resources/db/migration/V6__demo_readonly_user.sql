-- Cuenta PUBLICA de evaluación, solo consulta (VIEWER).
-- Esta migración no se ejecuta si faltan los datos ficticios o el administrador de prueba.
-- El hash BCrypt coincide con la contraseña PUBLICA actualmente indicada en el Post-it Vercel.
-- No reutilizar esta cuenta para datos personales ni producción empresarial.
-- No cambia contraseñas, roles, ni filas existentes.
INSERT INTO app_users
  (id, organization_id, email, password_hash, display_name, role, enabled, created_at)
SELECT UUID(),
       admin.organization_id,
       'demo@local.test',
       '$2y$12$pWKPjmjEqaUtkX/YjcIjX.8/e6JN/FlqLRTAYPpRHHYHXZQ.TdAgG',
       'Invitado de evaluación',
       'VIEWER',
       TRUE,
       UTC_TIMESTAMP(6)
FROM app_users AS admin
WHERE admin.email = 'admin@local.test'
  AND admin.role = 'ADMIN'
  AND admin.enabled = TRUE
  AND (SELECT COUNT(*) FROM customers AS c
       WHERE c.organization_id = admin.organization_id
         AND c.archived_at IS NULL) = 20
  AND (SELECT COUNT(*) FROM customers AS c
       WHERE c.organization_id = admin.organization_id
         AND c.archived_at IS NULL
         AND c.email LIKE '%.example') = 20
LIMIT 1
-- Un valor literal evita la referencia ambigua email=email cuando SELECT y destino usan app_users.
ON DUPLICATE KEY UPDATE email = 'demo@local.test';
