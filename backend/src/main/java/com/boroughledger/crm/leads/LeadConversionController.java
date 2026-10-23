package com.boroughledger.crm.leads;
import com.boroughledger.crm.users.AppUser;
import com.boroughledger.crm.opportunities.OpportunityService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/leads")
public class LeadConversionController {
 private final LeadConversionService service;
 public LeadConversionController(LeadConversionService service){this.service=service;}
 @PostMapping("/{id}/convert") @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')")
 public LeadConversionService.ConversionResult convert(@AuthenticationPrincipal AppUser actor,@PathVariable UUID id,@Valid @RequestBody LeadConversionService.Conversion r){return service.convert(actor,id,r);}
}
