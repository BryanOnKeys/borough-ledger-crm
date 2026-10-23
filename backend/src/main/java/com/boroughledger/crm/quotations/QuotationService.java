package com.boroughledger.crm.quotations;
import com.boroughledger.crm.shared.*;
import com.boroughledger.crm.products.*;
import com.boroughledger.crm.opportunities.*;
import com.boroughledger.crm.users.AppUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service public class QuotationService {
 private final QuotationRepository quotes;private final QuotationLineRepository lines;private final ProductRepository products;private final OpportunityRepository opportunities;private final AuditService audit;
 public QuotationService(QuotationRepository quotes,QuotationLineRepository lines,ProductRepository products,OpportunityRepository opportunities,AuditService audit){this.quotes=quotes;this.lines=lines;this.products=products;this.opportunities=opportunities;this.audit=audit;}
 public record LineInput(@NotNull UUID productId,@NotNull @DecimalMin(value="0.001") BigDecimal quantity,@NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal discountPercent,@NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal taxPercent){}
 public record Input(@NotNull UUID opportunityId,LocalDate validUntil,@Size(max=2000) String internalNotes,@NotEmpty List<@Valid LineInput> items){}
 public record Revision(LocalDate validUntil,@Size(max=2000) String internalNotes,@NotEmpty List<@Valid LineInput> items){}
 public record View(UUID id,String number,UUID opportunityId,String status,String currency,LocalDate validUntil,BigDecimal subtotal,BigDecimal discountTotal,BigDecimal taxTotal,BigDecimal total,String internalNotes,long version,List<QuotationLine> items){}
 @Transactional(readOnly=true) public Page<View> list(AppUser a,int page,int size){return quotes.findByOrganizationIdAndArchivedAtIsNull(a.getOrganizationId(),Checks.page(page,size)).map(q->view(q));}
 @Transactional(readOnly=true) public View get(AppUser a,UUID id){return view(find(a,id));}
 @Transactional public View create(AppUser a,Input input){
  Opportunity opp=opportunities.findByIdAndOrganizationIdAndArchivedAtIsNull(input.opportunityId(),a.getOrganizationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Oportunidad no encontrada"));
  Checks.require("ABIERTA".equals(opp.getStatus()),"Oportunidad cerrada");
  Quotation q=quotes.save(new Quotation(a.getOrganizationId(),opp.getId(),opp.getCurrency(),input.validUntil(),input.internalNotes()));
  saveLines(a,q,input.items());audit.log(a,"CREATED","QUOTATION",q.getId());return view(q);
 }
 @Transactional public View revise(AppUser a,UUID id,Revision r){Quotation q=find(a,id);Checks.require("BORRADOR".equals(q.getStatus()),"Solo se modifica una cotización en borrador");q.edit(r.validUntil(),r.internalNotes());lines.deleteByQuotationId(q.getId());lines.flush();saveLines(a,q,r.items());audit.log(a,"UPDATED","QUOTATION",id);return view(q);}
 @Transactional public View setStatus(AppUser a,UUID id,String next){Quotation q=find(a,id);String newStatus=Checks.oneOf(next,"ENVIADA","ACEPTADA","RECHAZADA");boolean allowed=("BORRADOR".equals(q.getStatus())&&"ENVIADA".equals(newStatus))||("ENVIADA".equals(q.getStatus())&&("ACEPTADA".equals(newStatus)||"RECHAZADA".equals(newStatus)));
 Checks.require(allowed,"Transición de cotización no permitida");if("ENVIADA".equals(newStatus))Checks.require(q.getTotal().signum()>0,"No se envía una cotización sin importe");q.status(newStatus);audit.log(a,newStatus,"QUOTATION",id);return view(q);}
 private View view(Quotation q){return new View(q.getId(),q.getNumber(),q.getOpportunityId(),q.getStatus(),q.getCurrency(),q.getValidUntil(),q.getSubtotal(),q.getDiscountTotal(),q.getTaxTotal(),q.getTotal(),q.getInternalNotes(),q.getVersion(),lines.findByQuotationIdOrderById(q.getId()));}
 private Quotation find(AppUser a,UUID id){return quotes.findByIdAndOrganizationIdAndArchivedAtIsNull(id,a.getOrganizationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Cotización no encontrada"));}
 private void saveLines(AppUser a,Quotation q,List<LineInput> inputs){Checks.require(inputs.size()<=100,"Máximo 100 líneas");
 BigDecimal gross=BigDecimal.ZERO,discount=BigDecimal.ZERO,tax=BigDecimal.ZERO,total=BigDecimal.ZERO;
 for(LineInput input:inputs){
 Product p=products.findByIdAndOrganizationIdAndArchivedAtIsNull(input.productId(),a.getOrganizationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Producto no encontrado"));
 Checks.require(Boolean.TRUE.equals(p.getActive()),"Producto inactivo");Checks.require(p.getCurrency().equals(q.getCurrency()),"Moneda del producto y cotización diferentes");
 QuotationMath.Breakdown r=QuotationMath.calculate(p.getUnitPrice(),input.quantity(),input.discountPercent(),input.taxPercent());
 lines.save(new QuotationLine(q.getId(),p.getId(),p.getName(),p.getSku(),p.getUnitPrice(),input.quantity(),input.discountPercent(),input.taxPercent(),r.total()));
 gross=gross.add(r.gross());discount=discount.add(r.discount());tax=tax.add(r.tax());total=total.add(r.total());
 }q.totals(gross,discount,tax,total);
 }
}
