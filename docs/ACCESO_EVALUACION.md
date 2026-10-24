# Acceso público de evaluación (Post-it)

El inicio de sesión de Borough & Ledger incluye un Post-it editorial. No debe utilizar las credenciales del administrador, porque el código del frontend es público y Vite incorpora las variables `VITE_*` al JavaScript del navegador.

## Habilitar el acceso de prueba

1. En el CRM público, entra con tu cuenta privada de administrador.
2. Abre **Usuarios → Nuevo usuario** y crea una cuenta nueva destinada exclusivamente a visitas: correo único que controles, nombre `Invitado de evaluación`, contraseña fuerte distinta de cualquier cuenta real y **rol `VIEWER`**. Ese rol no debe tener permisos de escritura en la API. Usa una organización donde únicamente haya registros ficticios; en el sistema actual verán los registros de la organización a la que pertenezca la nueva cuenta.
3. Comprueba por separado que el nuevo usuario entra, ve clientes y oportunidades y NO puede crear, editar ni borrar registros (el backend debe devolver 403 en operaciones prohibidas). No habilites acceso público hasta verificarlo.
4. Vercel → proyecto frontend → **Settings → Environment Variables**, crea `VITE_DEMO_EMAIL` y `VITE_DEMO_PASSWORD` con las credenciales de ese **usuario VIEWER**. Se trata de valores públicos, no secretos. El Post-it los muestra y permite rellenar automáticamente el formulario.
5. Aplica los valores a Production y realiza un nuevo **Redeploy** para que Vite los incorpore a la compilación.
6. Abre una ventana privada del navegador en tu URL Vercel y comprueba el Post-it, el inicio de sesión y el acceso de consulta.

Si las variables no están configuradas, el Post-it muestra un aviso de acceso próximo y no publica datos de acceso.

## Seguridad

- Cambia de inmediato la contraseña de administrador que hayas compartido antes por chat o capturas; no la reuses.
- Nunca introduzcas en variables `VITE_*` credenciales de `ADMIN`, `MANAGER`, `SALES`, MySQL ni claves privadas: cualquier visitante puede leerlas.
- Mantén contraseña única para el invitado, limita el número de usuarios, supervisa los accesos y revoca o cambia la clave cuando lo necesites.
- El CRM usa una única organización para datos comerciales: si esa organización contiene datos privados, crea una organización o entorno de pruebas separado antes de compartir la cuenta `VIEWER`. Los 348 registros SQL del proyecto son ficticios, pero nuevos registros creados manualmente podrían no serlo.
- `VITE_DEMO_PASSWORD` es una variable **pública por diseño**: no se considera un secreto.
