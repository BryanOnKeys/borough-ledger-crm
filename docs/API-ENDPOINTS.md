# Inventario de API REST v1

La API es JSON. Las rutas mutables requieren sesión HTTP con cookie y token CSRF. Salvo los endpoints públicos, `401` sin sesión; `403` sin permiso; `404` al buscar registro de otra organización; `409` en conflicto; `422` para transiciones inválidas.

## Identidad

- `GET /api/v1/status`: estado técnico público.
- `GET /api/v1/auth/csrf`: token antifalsificación de solicitudes (header indicado en respuesta).
- `POST /api/v1/auth/login`: `{ "email": "...", "password": "..." }`.
- `GET /api/v1/auth/me`: usuario autenticado.
- `POST /api/v1/auth/logout`: cierre de sesión.
- `POST /api/v1/profile/password`: contraseña actual y nueva.

## Configuración y administración

- `GET/PATCH /api/v1/organization`: organización actual.
- `GET/POST /api/v1/users`: administración (ADMIN).
- `PUT /api/v1/users/{id}`: perfil, rol y habilitación.
- `PATCH /api/v1/users/{id}/password`: restablecimiento administrativo.
- `GET /api/v1/audit`: consulta auditada (ADMIN/MANAGER).

## Módulos comerciales

- `GET/POST /api/v1/customers`; `GET/PUT/DELETE /api/v1/customers/{id}`.
- `GET/PATCH /api/v1/customers/{id}/details`: ficha ampliada (razón comercial, dirección, país, web, RUC/código, responsable).
- `GET/POST /api/v1/contacts`; `GET/PUT/DELETE /api/v1/contacts/{id}`.
- `GET/POST /api/v1/leads`; `GET/PUT/DELETE /api/v1/leads/{id}`.
- `POST /api/v1/leads/{id}/convert`: `{ "pipelineId": "UUID", "stageId": "UUID", "customerId": "UUID opcional", "contactId": "UUID opcional", "dealTitle":"...", "currency":"PEN" }`. Si no indicas cliente, se busca por nombre de empresa y se crea si falta; contacto por correo, evitando un duplicado evidente.
- `GET/POST /api/v1/pipelines`; `GET/PUT/DELETE /api/v1/pipelines/{id}`. Al crear uno se generan seis etapas iniciales.
- `GET/POST /api/v1/pipelines/{pipelineId}/stages`; `PUT /api/v1/pipelines/{pipelineId}/stages/{id}`.
- `GET/POST /api/v1/opportunities`; `GET/PUT/DELETE /api/v1/opportunities/{id}`.
- `PATCH /api/v1/opportunities/{id}/stage`: `{ "stageId":"UUID", "lossReason":"..." }`; motivo obligatorio en etapa PERDIDA.
- `GET /api/v1/opportunities/{id}/history`: historial de transiciones.
- `GET/POST /api/v1/activities`; `GET/PUT/DELETE /api/v1/activities/{id}`.
- `GET/POST /api/v1/tasks`; `GET/PUT/DELETE /api/v1/tasks/{id}`.
- `GET/POST /api/v1/products`; `GET/PUT/DELETE /api/v1/products/{id}`.
- `GET/POST /api/v1/quotations`; `GET/PUT /api/v1/quotations/{id}`.
- `PATCH /api/v1/quotations/{id}/status`: `{ "status": "ENVIADA|ACEPTADA|RECHAZADA" }`.
- `GET /api/v1/quotations/{id}/pdf`: documento descargable.

En los módulos CRUD se puede pasar `?page=0&size=20&search=texto` cuando corresponda. La búsqueda aplica al nombre o título, y a email en clientes.

## Indicadores e informes

- `GET /api/v1/dashboard/summary?currency=PEN`: totales de clientes, contactos, prospectos, oportunidades y tareas. Importes separados por moneda.
- `GET /api/v1/reports/sales?from=2026-01-01&to=2026-12-31`: ventas ganadas, por vendedor y moneda.
- `GET /api/v1/reports/funnel?currency=PEN`: distribución de oportunidades por etapa.
- `GET /api/v1/reports/sales.csv?from=2026-01-01&to=2026-12-31`: archivo CSV.

## Roles

`VIEWER`: lectura. `SALES`: escritura comercial general dentro de su organización. `MANAGER`: además archiva y consulta reportes. `ADMIN`: administra cuentas y organización. La visibilidad estricta *solo registros propios* aún no está aplicada: `SALES` puede leer los registros comerciales de su organización. Si el negocio la necesita, se debe añadir un modelo de permisos por responsable y pruebas antes de habilitar esa política.
