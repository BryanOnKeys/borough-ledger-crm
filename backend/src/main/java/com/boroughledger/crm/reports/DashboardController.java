package com.boroughledger.crm.reports;
import com.boroughledger.crm.users.AppUser;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/dashboard")
public class DashboardController {
 private final JdbcTemplate db;public DashboardController(JdbcTemplate db){this.db=db;}
 public record Summary(long clientes,long contactos,long prospectos,long oportunidadesAbiertas,long oportunidadesGanadas,long oportunidadesPerdidas,long tareasPendientes,long tareasVencidas,BigDecimal importeAbierto,BigDecimal importeGanado,String moneda){}
 @GetMapping("/summary") public Summary summary(@AuthenticationPrincipal AppUser a,@RequestParam(defaultValue="PEN") String currency){
  if(!currency.matches("[A-Z]{3}"))throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Moneda inválida");
  String org=a.getOrganizationId().toString();
  return new Summary(count("customers","",org),count("contacts","",org),count("leads"," AND status!='CONVERTIDO'",org),count("opportunities"," AND status='ABIERTA'",org),count("opportunities"," AND status='GANADA'",org),count("opportunities"," AND status='PERDIDA'",org),count("crm_tasks"," AND status IN ('PENDIENTE','EN_PROGRESO')",org),count("crm_tasks"," AND due_at < UTC_TIMESTAMP() AND status IN ('PENDIENTE','EN_PROGRESO')",org),amount(org,"ABIERTA",currency),amount(org,"GANADA",currency),currency);
 }
 private long count(String table,String extra,String org){return db.queryForObject("SELECT COUNT(*) FROM "+table+" WHERE organization_id=? AND archived_at IS NULL"+extra,Long.class,org);}
 private BigDecimal amount(String org,String status,String currency){BigDecimal result=db.queryForObject("SELECT COALESCE(SUM(amount),0) FROM opportunities WHERE organization_id=? AND archived_at IS NULL AND status=? AND currency=?",BigDecimal.class,org,status,currency);return result==null?BigDecimal.ZERO:result;}
}
