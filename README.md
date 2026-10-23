# Borough & Ledger CRM

CRM para operaciones comerciales, desarrollado con Java 25, Spring Boot 4, MySQL 8, React y TypeScript. Sistema organizado por módulos, con autenticación por sesión, permisos, trazabilidad y persistencia relacional.

> Estado: código de desarrollo. Las pruebas y la seguridad de un despliegue público deben validarse antes de permitir el acceso de terceros. El repositorio no representa una certificación de producción.

## Módulos

- Clientes, contactos y prospectos
- Oportunidades y embudos de ventas
- Actividades y tareas
- Productos, servicios y cotizaciones
- Indicadores y reportes comerciales
- Usuarios, roles, organización y auditoría

## Organización del repositorio

```text
borough-ledger-crm/
├── backend/           API Spring Boot, pruebas, migraciones Flyway
├── frontend/          Aplicación React / TypeScript
├── sql/               Creación de base y carga opcional de datos ficticios
├── docs/              Arquitectura, endpoints y configuración
├── .github/workflows/ CI para código backend y frontend
├── .env.example       Plantilla de variables sin contraseñas reales
└── README.md
```

## Requisitos

- JDK 25, Maven y MySQL 8.x, Node.js y npm.
- Eclipse/Spring Tools para backend y VS Code para frontend son opciones de trabajo.
- Usuario de MySQL con permisos para crear la base de datos y el usuario de aplicación.

## Preparar la base de datos

1. Abre `sql/01_crear_base_y_usuario.sql` en MySQL Workbench, reemplaza **localmente** `CAMBIA_LA_CLAVE` por una contraseña privada y ejecútalo con permisos de administrador.
2. Configura en Eclipse/terminal `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `CRM_BOOTSTRAP_EMAIL` y `CRM_BOOTSTRAP_PASSWORD` (mínimo 12 caracteres). Consulta `.env.example`, que solo contiene marcadores.
3. Ejecuta backend con perfil `dev` por primera vez. Flyway aplica V1–V5 y, si todavía no hay usuarios, el inicializador local puede crear el administrador.
4. Opcionalmente, con el esquema migrado, ejecuta `sql/02_datos_operativos_ficticios.sql` en MySQL Workbench. Por defecto busca `admin@local.test`; cambia ese correo en el script si tu administrador es otro.
5. Para añadir ocho empresas ficticias más, ejecuta `sql/03_clientes_adicionales_ficticios.sql` (mismo requisito de correo).
6. Ejecuta `sql/04_verificar_datos.sql` para inspeccionar datos y relaciones.

**Flyway construye el esquema completo** desde `backend/src/main/resources/db/migration`. Los SQL de `02` y `03` son semillas opcionales para desarrollo, NO migraciones Flyway. No los ejecutes automáticamente en producción.

## Iniciar backend

```powershell
# Ejemplo de variables: reemplaza en tu PC, NO copies las claves al repositorio
$env:DB_URL = 'jdbc:mysql://localhost:3306/borough_ledger?connectionTimeZone=UTC'
$env:DB_USERNAME = 'crm_app'
$env:DB_PASSWORD = '<TU_CLAVE_PRIVADA>'
$env:CRM_BOOTSTRAP_EMAIL = 'admin@local.test'
$env:CRM_BOOTSTRAP_PASSWORD = '<OTRA_CLAVE_PRIVADA_DE_12_O_MAS_CARACTERES>'
cd backend
mvn spring-boot:run '-Dspring-boot.run.profiles=dev'
```

Si ejecutas el backend desde Eclipse, agrega las variables en **Run Configurations → Environment** y el argumento `--spring.profiles.active=dev`. Las variables de una consola PowerShell no se heredan automáticamente a Eclipse abierto.

## Iniciar frontend

En otra consola:

```powershell
cd frontend
npm install
npm run dev
```

Abre `http://127.0.0.1:5173`. Para compilar, usa `npm run build`. No hay ninguna contraseña real incluida en este repositorio.

## Pruebas

```powershell
cd backend
mvn clean test
mvn verify     # Testcontainers requiere Docker
```

El workflow de GitHub Actions intenta comprobar ambas partes. No se garantiza que haya pasado hasta revisar la ejecución real de Actions.

## Publicación y acceso de terceros

GitHub **almacena código, migraciones y datos ficticios**, pero **no aloja por sí solo una instancia MySQL persistente ni un proceso Spring Boot en ejecución**. Para hacer el CRM accesible desde tu portafolio necesitas desplegar API, MySQL y frontend; configurar HTTPS, cookies/CSRF, CORS y backups.

**Nunca publiques** las credenciales del administrador, `root`, `crm_app`, archivos `.env`, ni respaldos de la base que contengan datos propios o de personas reales. Un acceso de evaluación debe ser una cuenta separada de permisos limitados y utilizar únicamente datos ficticios. Antes de compartir credenciales de evaluación, se requiere un entorno desplegado y aislado. Consulta `docs/PUBLICACION-Y-ACCESO.md`.

## Limitaciones conocidas

- Los tests end-to-end y la revisión de seguridad en un hosting público no están certificados.
- La carga SQL es un conjunto de datos ficticios reproducible, no un respaldo en vivo de la base que ya utilizas en tu equipo.
- La creación de cuentas y recuperación de acceso por correo están sujetas a la configuración disponible; no se afirma envío real de correos.

## Documentación

- `docs/ARQUITECTURA.md`
- `docs/API-ENDPOINTS.md`
- `docs/VALIDACION.md`
- `docs/PUBLICACION-Y-ACCESO.md`
- `docs/COMANDOS-GITHUB.md`
- `docs/INTEGRACION-FRONTEND.md`
