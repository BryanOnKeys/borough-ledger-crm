package com.boroughledger.crm.integration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}
