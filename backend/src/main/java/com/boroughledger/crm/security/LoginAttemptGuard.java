package com.boroughledger.crm.security;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
@Component public class LoginAttemptGuard {
 private record Attempt(int failures, Instant since){}
 private final ConcurrentHashMap<String,Attempt> attempts=new ConcurrentHashMap<>();
 public void verify(String ip){Attempt a=attempts.get(ip);if(a!=null&&a.failures()>=8&&Instant.now().isBefore(a.since().plusSeconds(900)))throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Demasiados intentos. Vuelve a intentarlo más tarde.");}
 public void fail(String ip){attempts.compute(ip,(k,v)->v==null||Instant.now().isAfter(v.since().plusSeconds(900))?new Attempt(1,Instant.now()):new Attempt(v.failures()+1,v.since()));}
 public void reset(String ip){attempts.remove(ip);}
}
