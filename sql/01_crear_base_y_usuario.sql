-- Ejecutar con usuario administrador MySQL y reemplazar la contraseña ANTES de ejecutarlo.
CREATE DATABASE IF NOT EXISTS borough_ledger CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'crm_app'@'localhost' IDENTIFIED BY 'CAMBIA_LA_CLAVE';
ALTER USER 'crm_app'@'localhost' IDENTIFIED BY 'CAMBIA_LA_CLAVE';
GRANT ALL PRIVILEGES ON borough_ledger.* TO 'crm_app'@'localhost';
-- Las tablas son gestionadas exclusivamente mediante Flyway V1..V5.
