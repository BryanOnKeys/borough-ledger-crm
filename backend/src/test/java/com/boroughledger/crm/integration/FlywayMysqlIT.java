package com.boroughledger.crm.integration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class FlywayMysqlIT {
    @Container
    static final MySQLContainer mysql = new MySQLContainer("mysql:8.4")
        .withDatabaseName("borough_ledger")
        .withUsername("crm_app")
        .withPassword("local-only-test-password");

    static JdbcTemplate db;

    @BeforeAll
    static void migrateSchema() {
        // Ejecuta las migraciones reales con MySQL, sin levantar la seguridad web.
        Flyway.configure()
            .dataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword())
            .locations("classpath:db/migration")
            .load()
            .migrate();
        db = new JdbcTemplate(new DriverManagerDataSource(
            mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword()));
    }

    @Test
    void migratesCoreCommercialSchema() {
        Integer n = db.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=database() AND table_name IN ('customers','contacts','leads','opportunities','quotations','quotation_items','crm_tasks','audit_logs')",
            Integer.class);
        assertEquals(8, n);
    }

    @Test
    void demoMigrationHandlesExistingViewerWithoutChangingItsRole() throws Exception {
        String orgId = UUID.randomUUID().toString();
        db.update("INSERT INTO organizations (id,name,created_at) VALUES (?, ?, UTC_TIMESTAMP(6))",
            orgId, "Organización ficticia");
        db.update("INSERT INTO app_users (id,organization_id,email,password_hash,display_name,role,enabled,created_at) VALUES (?,?,'admin@local.test','test-only','Admin de pruebas','ADMIN',TRUE,UTC_TIMESTAMP(6))",
            UUID.randomUUID().toString(), orgId);
        for (int i = 0; i < 20; i++) {
            db.update("INSERT INTO customers (id,organization_id,name,email,status,created_at,updated_at) VALUES (?,?,?,?,'ACTIVE',UTC_TIMESTAMP(6),UTC_TIMESTAMP(6))",
                UUID.randomUUID().toString(), orgId, "Cliente ficticio " + i, "cliente" + i + "@demo.example");
        }
        // Reproduce el escenario real: el invitado ya se creó manualmente en Aiven.
        db.update("INSERT INTO app_users (id,organization_id,email,password_hash,display_name,role,enabled,created_at) VALUES (?,?,'demo@local.test','test-hash','Invitado','VIEWER',TRUE,UTC_TIMESTAMP(6))",
            UUID.randomUUID().toString(), orgId);

        String sql = new String(new ClassPathResource("db/migration/V6__demo_readonly_user.sql")
            .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        db.execute(sql);
        db.execute(sql);
        assertEquals(1, db.queryForObject(
            "SELECT COUNT(*) FROM app_users WHERE email='demo@local.test' AND password_hash='test-hash' AND role='VIEWER'",
            Integer.class));
        assertEquals(20, db.queryForObject(
            "SELECT COUNT(*) FROM customers WHERE organization_id=?", Integer.class, orgId));
    }
}
