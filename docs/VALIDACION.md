# Comprobaciones antes de declarar la API lista

Ejecuta **en tu Windows**:

```powershell
cd backend
mvn clean test
mvn verify       # requiere Docker para FlywayMysqlIT
mvn spring-boot:run '-Dspring-boot.run.profiles=dev'
```

Para migrar datos reales por primera vez:

1. Exporta `borough_ledger` desde Workbench y comprueba que el respaldo puede restaurarse.
2. Confirma que `flyway_schema_history` contiene V1 con `success = 1`.
3. Arranca el backend con sus variables previas. Comprueba que aparecen V2, V3, V4 y V5 como correctas.
4. Verifica que tus 8 clientes anteriores sigan presentes y estén accesibles desde el mismo administrador.
5. Prueba login con `/auth/csrf` y su cabecera para cada operación mutable.
6. Crea empresa/contacto/prospecto/embudo y lista etapas; crea una oportunidad, muévela, verifica historial.
7. Registra un producto en PEN, crea cotización con impuesto y descuento; revisa total y PDF; márcala ENVIADA y ACEPTADA; intenta editarla: debe devolver error.
8. Comprueba que `dashboard/summary` cambió por los registros persistidos, no por el frontend.
9. Crea un usuario VIEWER e intenta POST/PUT/DELETE: debe obtener 403.
10. Crea otra organización y usuario en una base de prueba controlada; verifica que no puede consultar por UUID un registro de la primera organización.
11. Reinicia Spring Boot y comprueba que siguen presentes todos los registros.
12. Usa dos sesiones para editar el mismo registro y evalúa conflictos de concurrencia.

**Estado de esta entrega:** análisis estático y XML del POM comprobados; `QuotationMath.java` compiló aisladamente con JDK 21 disponible. Sin build Maven, sin ejecutar Spring Boot contra MySQL y sin Testcontainers. No se afirma que las pruebas pasaron.
