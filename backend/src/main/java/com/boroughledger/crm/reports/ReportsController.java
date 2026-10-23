package com.boroughledger.crm.reports;
import com.boroughledger.crm.users.AppUser;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/reports") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class ReportsController {
 private final JdbcTemplate db;public ReportsController(JdbcTemplate db){this.db=db;}
 public record Sales(String vendedorId,String moneda,long oportunidadesGanadas,BigDecimal monto){}
 public record Funnel(String etapaId,String etapa,long oportunidades,BigDecimal total,String moneda){}
 @GetMapping("/sales") public List<Sales> sales(@AuthenticationPrincipal AppUser actor,@RequestParam LocalDate from,@RequestParam LocalDate to){
  range(from,to);return db.query("SELECT COALESCE(owner_id,'SIN_ASIGNAR') vendedor, currency,COUNT(*) total,COALESCE(SUM(amount),0) importe FROM opportunities WHERE organization_id=? AND status='GANADA' AND archived_at IS NULL AND closed_at>=? AND closed_at<=? GROUP BY owner_id,currency ORDER BY importe DESC",(rs,n)->new Sales(rs.getString("vendedor"),rs.getString("currency"),rs.getLong("total"),rs.getBigDecimal("importe")),actor.getOrganizationId().toString(),from,to);
 }
 @GetMapping("/funnel") public List<Funnel> funnel(@AuthenticationPrincipal AppUser actor,@RequestParam(defaultValue="PEN") String currency){
  if(!currency.matches("[A-Z]{3}"))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Moneda inválida");
  return db.query("SELECT s.id sid,s.name stage,COUNT(o.id) total,COALESCE(SUM(o.amount),0) amount FROM pipeline_stages s LEFT JOIN opportunities o ON o.stage_id=s.id AND o.organization_id=s.organization_id AND o.archived_at IS NULL AND o.currency=? WHERE s.organization_id=? AND s.archived_at IS NULL GROUP BY s.id,s.name ORDER BY MIN(s.position_index)",(rs,n)->new Funnel(rs.getString("sid"),rs.getString("stage"),rs.getLong("total"),rs.getBigDecimal("amount"),currency),currency,actor.getOrganizationId().toString());
 }
 @GetMapping(value="/sales.csv",produces="text/csv;charset=UTF-8") public ResponseEntity<byte[]> csv(@AuthenticationPrincipal AppUser a,@RequestParam LocalDate from,@RequestParam LocalDate to){StringBuilder out=new StringBuilder("vendedor_id,moneda,oportunidades_ganadas,monto\r\n");for(Sales row:sales(a,from,to)){out.append(csvCell(row.vendedorId())).append(',').append(csvCell(row.moneda())).append(',').append(row.oportunidadesGanadas()).append(',').append(row.monto()).append("\r\n");}return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=ventas.csv").body(out.toString().getBytes(StandardCharsets.UTF_8));}
 private String csvCell(String cell){if(cell==null)return "";String value=cell; // Protección frente a fórmulas al abrir CSV en hojas de cálculo
  if(!value.isEmpty()&&"=+-@".indexOf(value.charAt(0))>=0)value="'"+value;
  return "\""+value.replace("\"","\"\"")+"\"";}
 private void range(LocalDate from,LocalDate to){if(from.isAfter(to)||from.plusYears(5).isBefore(to))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Rango de fechas inválido");}
}
