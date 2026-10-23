package com.boroughledger.crm.shared;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
@Component
public class TenantGuard {
 private final EntityManager em;
 public TenantGuard(EntityManager em){this.em=em;}
 public void active(String entity,UUID id,UUID org) {
  if(id==null) return;
  // Claves permitidas y predefinidas: nunca se concatena una entidad recibida por HTTP.
  if(!java.util.Set.of("Customer","Contact","Lead","Pipeline","PipelineStage","Opportunity","Activity","TaskItem","Product","Quotation").contains(entity)) throw new IllegalArgumentException("Entidad no admitida");
  Long count=em.createQuery("select count(e) from "+entity+" e where e.id=:id and e.organizationId=:org and e.archivedAt is null",Long.class)
  .setParameter("id",id).setParameter("org",org).getSingleResult();
  if(count==0) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Referencia relacionada inexistente o no autorizada");
 }
}
