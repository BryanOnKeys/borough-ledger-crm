package com.boroughledger.crm.shared;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.math.BigDecimal;
public final class Checks {
 private Checks(){}
 public static void require(boolean yes,String message) {if(!yes) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,message);}
 public static PageRequest page(int page,int size) {
  if(page<0||size<1||size>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Paginación inválida (size: 1..100)");
  return PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt"));
 }
 public static String oneOf(String value,String... allowed){ for(String a:allowed) if(a.equals(value)) return value; throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Estado no permitido: "+value); }
 public static void nonnegative(BigDecimal v,String field){require(v!=null&&v.signum()>=0,field+" debe ser mayor o igual a cero");}
}
