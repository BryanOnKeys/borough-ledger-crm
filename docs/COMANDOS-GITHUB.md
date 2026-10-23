# Publicar en GitHub desde Windows PowerShell

1. En GitHub crea un repositorio nuevo llamado `borough-ledger-crm`, sin README ni .gitignore añadidos por GitHub (ya existen localmente).
2. Descomprime este proyecto y ábrelo desde VS Code usando **File → Open Folder** (la carpeta que contiene backend, frontend, sql, docs).
3. Abre una terminal PowerShell en la raíz y ejecuta:

```powershell
git --version
git init -b main
git status
git add .
git status --short
git commit -m "feat: proyecto CRM full stack con migraciones y datos ficticios"
git remote add origin https://github.com/TU_USUARIO/borough-ledger-crm.git
git push -u origin main
```

Reemplaza `TU_USUARIO` por tu nombre de usuario de GitHub. GitHub puede pedir autenticación desde el navegador o un gestor de credenciales; no pegues contraseñas en mensajes.

4. Antes de confirmar `git commit`, revisa `git status --short` y que NO haya `.env`, `node_modules`, `target`, `dist` ni exportaciones `*.dump` o `*.sql.gz`.
5. Para actualizar el proyecto después:

```powershell
git add .
git commit -m "fix: ajustes de interfaz y servicios del CRM"
git push
```

Si ya habías creado un repositorio Git local o remoto, comprueba `git status` y `git remote -v` antes de usar `git init` o `remote add`, para no duplicar una conexión ya existente.

### Nota sobre contraseñas

Un `.gitignore` evita nuevas incorporaciones accidentales de archivos ignorados, pero no elimina secretos que ya estén en commits anteriores. Ante exposición de una clave, cámbiala inmediatamente.
