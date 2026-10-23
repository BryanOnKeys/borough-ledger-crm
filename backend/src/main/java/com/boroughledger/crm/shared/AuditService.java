package com.boroughledger.crm.shared;
import com.boroughledger.crm.audit.*;
import com.boroughledger.crm.users.AppUser;
import java.util.UUID;
import org.springframework.stereotype.Service;
@Service public class AuditService {
 private final AuditRepository repository;
 public AuditService(AuditRepository repository){this.repository=repository;}
 public void log(AppUser actor,String action,String type,UUID id){repository.save(new AuditLog(actor.getOrganizationId(),actor.getId(),action,type,id));}
}
