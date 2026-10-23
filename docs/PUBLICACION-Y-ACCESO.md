# Preparar publicación y acceso de evaluación

## Lo que sí guardamos públicamente

- Código Java/React, migraciones SQL V1–V5 y scripts con información empresarial ficticia.
- Documentación para replicar el esquema e importar el dataset.
- Archivo `.env.example` con nombres de variables, nunca valores reales.

## Lo que se mantiene fuera de GitHub

- Contraseñas `root`, `crm_app`, administrador y contraseñas de cualquier evaluador.
- Exportaciones completas de una base de datos usada personalmente, cookies/tokens y archivos de configuración privados.
- Información personal real de clientes, proveedores o empleados.

## Para que otra persona pruebe la aplicación

1. Verifica que el backend inicia, ejecuta Flyway y que los endpoints funcionan con MySQL.
2. Publica la base de datos en un proveedor que permita conexiones privadas desde tu backend; habilita backups.
3. Publica Spring Boot con HTTPS, secretos administrados y política de sesión/cookies/CSRF apropiada.
4. Publica React, por ejemplo en Vercel, conectándolo a la API bajo un mismo origen/proxy cuando sea posible.
5. Crea una organización independiente con datos ficticios para las pruebas. En el CRM, crea una cuenta **VIEWER** para acceso de solo lectura o una cuenta SALES con permisos restringidos y una estrategia de restablecimiento de datos si necesitan probar modificaciones.
6. Verifica el aislamiento por organización y las restricciones de acceso antes de entregar las credenciales de esa cuenta.
7. Solo entonces añade al README la URL pública y el usuario limitado; comunica la clave a los evaluadores por un canal privado o usa un acceso temporal. No compartas tus credenciales locales.

El backend actual no implementa aislamiento de sesiones públicas efímeras ni restablecimiento automatizado de cambios de visitantes. Por eso no debe anunciarse como una instancia de demo público con edición libre sin desarrollar primero esas medidas.

## Backup privado del estado real

En MySQL Workbench: `Server → Data Export`, selecciona `borough_ledger`, exporta esquema + datos a un archivo local y guárdalo fuera del proyecto (preferentemente cifrado). Esto sí puede conservar tus cambios privados. No incluyas ese archivo en el repositorio público.
