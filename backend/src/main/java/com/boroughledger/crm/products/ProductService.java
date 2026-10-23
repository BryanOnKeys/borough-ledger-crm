package com.boroughledger.crm.products;
import com.boroughledger.crm.shared.*;
import com.boroughledger.crm.users.AppUser;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class ProductService {
 private final ProductRepository repository; private final TenantGuard guard; private final AuditService audit; 
 public ProductService(ProductRepository repository,TenantGuard guard,AuditService audit){this.repository=repository;this.guard=guard;this.audit=audit;}
 @Transactional(readOnly=true) public Page<ProductDto.Response> list(AppUser actor,int page,int size,String search){
  return repository.search(actor.getOrganizationId(),search==null||search.isBlank()?null:search.strip(),Checks.page(page,size)).map(ProductDto.Response::from);
 }
 @Transactional(readOnly=true) public ProductDto.Response get(AppUser actor,UUID id){return ProductDto.Response.from(find(actor,id));}
 @Transactional public ProductDto.Response create(AppUser actor,ProductDto.Request r){
  validate(actor,r); Product obj=repository.save(new Product(actor.getOrganizationId(),r)); audit.log(actor,"CREATED","PRODUCT",obj.getId());return ProductDto.Response.from(obj);
 }
 @Transactional public ProductDto.Response update(AppUser actor,UUID id,ProductDto.Request r){
  validate(actor,r);Product obj=find(actor,id);obj.update(r);audit.log(actor,"UPDATED","PRODUCT",id);return ProductDto.Response.from(obj);
 }
 @Transactional public void archive(AppUser actor,UUID id){Product obj=find(actor,id);obj.archive();audit.log(actor,"ARCHIVED","PRODUCT",id);}
 private Product find(AppUser a,UUID id){return repository.findByIdAndOrganizationIdAndArchivedAtIsNull(id,a.getOrganizationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado"));}
 private void validate(AppUser actor,ProductDto.Request r){
  Checks.oneOf(r.kind(),"PRODUCTO","SERVICIO");

 }
}
