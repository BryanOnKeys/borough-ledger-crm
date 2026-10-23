-- BOROUGH & LEDGER CRM - 8 clientes ficticios adicionales para uso local
-- Requiere que Spring Boot haya arrancado en perfil dev al menos una vez,
-- creando admin@local.test y su organizacion. No crea ni modifica contrasenas.
-- Seguro de reejecutar: los IDs fijos no generan copias adicionales.

USE borough_ledger;

-- Asociar los clientes a la misma organizacion del usuario administrador.
SET @org_id = (
    SELECT organization_id
    FROM app_users
    WHERE email = 'admin@local.test'
    LIMIT 1
);

-- Verificar que la organizacion existe antes de ejecutar la insercion.
SELECT @org_id AS organization_id_del_administrador;

-- Si el valor de arriba es NULL, inicia Spring Boot con el perfil dev
-- y revisa que haya creado al administrador. No ejecutes el INSERT hasta entonces.

INSERT INTO customers
    (id, organization_id, name, email, phone, industry, status,
     created_at, updated_at, archived_at, version)
VALUES
    ('292d845c-71cc-5a34-82eb-e3745ee5f7fa', @org_id,
     'Andina Logistica S.A.C.', 'contacto@andinalogistica.example',
     '000-000-001', 'Logistica', 'ACTIVE', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), NULL, 0),
    ('d186e2fb-659f-55bb-a427-da614264fa97', @org_id,
     'Prisma Tecnologia S.A.C.', 'ventas@prismatecnologia.example',
     '000-000-002', 'Tecnologia', 'ACTIVE', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), NULL, 0),
    ('dfda66f1-fb2d-5fae-8c52-e6683b76a6dc', @org_id,
     'Norte Textil S.A.C.', 'comercial@nortetextil.example',
     '000-000-003', 'Textil', 'ACTIVE', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), NULL, 0),
    ('81e45440-65cb-5020-ab23-452b51d2e412', @org_id,
     'Nexo Retail S.A.C.', 'contacto@nexoretail.example',
     '000-000-004', 'Comercio', 'ACTIVE', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), NULL, 0),
    ('cb6c7861-a73b-5e64-aaf3-b58d89efaa81', @org_id,
     'Altura Ingenieria S.A.C.', 'proyectos@alturaingenieria.example',
     '000-000-005', 'Ingenieria', 'ACTIVE', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), NULL, 0),
    ('6b027902-6aa7-58a9-9327-057598332ebf', @org_id,
     'Lumen Consultoria S.A.C.', 'hola@lumenconsultoria.example',
     '000-000-006', 'Consultoria', 'ACTIVE', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), NULL, 0),
    ('9a76e1dd-1982-58dd-a4d3-d92b6e2538eb', @org_id,
     'Pacifico Diseno S.A.C.', 'estudio@pacificodiseno.example',
     '000-000-007', 'Diseno', 'ACTIVE', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), NULL, 0),
    ('c1893ae1-814f-5348-8ca1-6de004f6e949', @org_id,
     'Mercurio Distribucion S.A.C.', 'ventas@mercuriodistribucion.example',
     '000-000-008', 'Distribucion', 'ACTIVE', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), NULL, 0)
ON DUPLICATE KEY UPDATE id = id;

-- Verificar que los registros quedan almacenados y visibles para el admin.
SELECT name, email, industry, status
FROM customers
WHERE organization_id = @org_id AND archived_at IS NULL
ORDER BY name;
