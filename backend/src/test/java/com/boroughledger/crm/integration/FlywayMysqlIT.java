package com.boroughledger.crm.integration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.NONE)
class FlywayMysqlIT {
 @Container static final MySQLContainer mysql=new MySQLContainer("mysql:8.4").withDatabaseName("borough_ledger").withUsername("crm_app").withPassword("local-only-test-password");
 @DynamicPropertySource static void config(DynamicPropertyRegistry r){
  r.add("spring.datasource.url",mysql::getJdbcUrl);r.add("spring.datasource.username",mysql::getUsername);r.add("spring.datasource.password",mysql::getPassword);
 }
 @Autowired JdbcTemplate db;
 @Test void migratesCoreCommercialSchema(){
  Integer n=db.queryForObject("select count(*) from information_schema.tables where table_schema=database() and table_name in ('customers','contacts','leads','opportunities','quotations','quotation_items','crm_tasks','audit_logs')",Integer.class);
  assertEquals(8,n);
 }

 @Test void demoMigrationHandlesExistingViewerWithoutChangingItsRole() throws Exception {
  String organizationId=UUID.randomUUID().toString();
  db.update("INSERT INTO organizations (id,name,created_at) VALUES (?, ?, UTC_TIMESTAMP(6))",
      organizationId, "Organización ficticia");
  db.update("INSERT INTO app_users (id,organization_id,email,password_hash,display_name,role,enabled,created_at) VALUES (?,?,'admin@local.test','test-only','Admin de pruebas','ADMIN',TRUE,UTC_TIMESTAMP(6))",
      UUID.randomUUID().toString(), organizationId);
  for(int i=0;i<20;i++){
    db.update("INSERT INTO customers (id,organization_id,name,email,status,created_at,updated_at) VALUES (?,?,?,?,'ACTIVE',UTC_TIMESTAMP(6),UTC_TIMESTAMP(6))",
      UUID.randomUUID().toString(), organizationId, "Cliente ficticio "+i, "cliente"+i+"@demo.example");
  }
  db.update("INSERT INTO app_users (id,organization_id,email,password_hash,display_name,role,enabled,created_at) VALUES (?,?,'demo@local.test','test-hash','Invitado','VIEWER',TRUE,UTC_TIMESTAMP(6))",
      UUID.randomUUID().toString(), organizationId);
  String sql=new String(new ClassPathResource("db/migration/V6__demo_readonly_user.sql").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
  db.execute(sql);
  db.execute(sql);
  assertEquals(1, db.queryForObject("SELECT COUNT(*) FROM app_users WHERE email='demo@local.test' AND password_hash='test-hash' AND role='VIEWER'", Integer.class));
  assertEquals(20, db.queryForObject("SELECT COUNT(*) FROM customers WHERE organization_id=?", Integer.class,organizationId));
 }
}
