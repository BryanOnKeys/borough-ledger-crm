# Arquitectura técnica

## Monolito modular

```text
HTTP/HTTPS → SecurityFilterChain (sesión, CSRF, RBAC)
          → Controllers: /api/v1/*
          → Application Services (@Transactional)
          → Repositories/JdbcTemplate
          → MySQL 8.4
          → Flyway V1..V5
```

Cada módulo del dominio incluye JPA Entity, DTO, Repository, Service y Controller. Entidades nuevas derivan de `TenantRecord` (UUID, organizationId, auditoría temporal, archivado lógico, versión optimista). Los clientes de la versión anterior mantienen su entidad histórica.

## Módulos

`auth`, `security`, `users`, `organizations`, `customers`, `contacts`, `leads`, `pipelines`, `opportunities`, `activities`, `tasks`, `products`, `quotations`, `reports`, `audit`, `shared`.

**Transacciones importantes**: conversión de lead; creación/revisión de cotización; cambios de etapa e historial. La auditoría se escribe en la misma transacción comercial. Los informes ejecutan consultas agregadas sobre la base; no calculan KPIs a partir de arreglos del navegador.

## Relaciones básicas

```text
organizations ─┬─ app_users
               ├─ customers ── contacts
               ├─ leads
               ├─ pipelines ── pipeline_stages
               ├─ opportunities ── opportunity_stage_history
               │                   └── quotations ── quotation_items ── products
               ├─ activities
               ├─ crm_tasks
               └─ audit_logs
```

`opportunities.customer_id` y `opportunities.stage_id` son relaciones con clave foránea. En la capa de servicio se verifica el tenant en toda referencia comercial. Un `organizationId` arbitrario enviado por HTTP nunca se usa como autoridad. Si se espera uso multi-organización intensivo, conviene añadir además claves compuestas `(organization_id, id)` en todas las relaciones para proteger la consistencia también frente a cambios SQL externos.

## Migraciones

- `V1__initial_schema.sql`: datos existentes (NO modificar, para conservar checksum de Flyway).
- `V2__business_modules.sql`: contactos, leads, pipelines, actividades, tareas, catálogo.
- `V3__deals_and_quotations.sql`: etapas, oportunidades, historial, cotizaciones y líneas.
- `V4__referential_integrity.sql`: claves foráneas e índices.
- `V5__customer_profile.sql`: ficha ampliada de clientes sin eliminar campos antiguos.

En entornos nuevos todas se ejecutan en orden. Si ya existe V1, Flyway continúa en V2. Verifica previamente respaldos, permisos y versión exacta de MySQL.

## Monetario

Los precios son `DECIMAL` en la BD y `BigDecimal` en Java. Para cada línea: bruto = precio × cantidad; descuento = bruto × porcentaje; base gravable = bruto − descuento; impuesto = base × porcentaje; total = base + impuesto. Se redondea a dos decimales con `HALF_UP` por línea. Cotizaciones aceptadas no se editan y las líneas conservan precio/nombre/SKU histórico.

## Límites y pendientes de endurecimiento para producción

No se ofrece aún registro público de organizaciones, SSO, cifrado en reposo de campos, recuperación por correo, bloqueo de sesiones distribuidas, notificaciones automáticas, reglas de asignación por responsable, observabilidad centralizada ni tests de carga. El limitador de intentos de login es en memoria. Debe comprobarse la configuración de CORS, proxy, TLS y cookies en el dominio concreto de producción.
