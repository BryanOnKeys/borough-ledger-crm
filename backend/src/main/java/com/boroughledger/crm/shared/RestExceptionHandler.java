package com.boroughledger.crm.shared;
import java.util.Map;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class RestExceptionHandler {
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<ProblemDetail> invalidInput(MethodArgumentNotValidException e){
  ProblemDetail detail=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Verifica los campos enviados.");detail.setTitle("Datos inválidos");detail.setProperty("errors",e.getBindingResult().getFieldErrors().stream().map(f->Map.of("field",f.getField(),"message",f.getDefaultMessage()==null?"Inválido":f.getDefaultMessage())).toList());return ResponseEntity.badRequest().body(detail);
 }
 @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
 public ResponseEntity<ProblemDetail> conflict(ObjectOptimisticLockingFailureException e){return ResponseEntity.status(409).body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,"Este registro fue modificado por otra persona. Actualiza la vista."));}
 @ExceptionHandler(DataIntegrityViolationException.class)
 public ResponseEntity<ProblemDetail> integrity(DataIntegrityViolationException e){return ResponseEntity.status(409).body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,"Operación incompatible con los datos existentes."));}
 @ExceptionHandler(ConstraintViolationException.class)
 public ResponseEntity<ProblemDetail> constraints(ConstraintViolationException e){return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Parámetros inválidos."));}
}
