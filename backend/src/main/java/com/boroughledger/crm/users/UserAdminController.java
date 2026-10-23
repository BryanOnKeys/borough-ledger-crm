package com.boroughledger.crm.users;
import com.boroughledger.crm.shared.*;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {
 private final UserRepository users;private final PasswordEncoder encoder;private final AuditService audit;
 public UserAdminController(UserRepository users,PasswordEncoder encoder,AuditService audit){this.users=users;this.encoder=encoder;this.audit=audit;}
 public record NewUser(@NotBlank @Email String email,@NotBlank @Size(min=2,max=120) String displayName,@NotBlank @Size(min=12,max=100) String password,@NotBlank String role){}
 public record UpdateUser(@NotBlank @Size(min=2,max=120) String displayName,@NotBlank String role,boolean enabled){}
 public record PasswordChange(@NotBlank @Size(min=12,max=100) String password){}
 public record View(UUID id,String email,String displayName,String role,boolean enabled){static View from(AppUser u){return new View(u.getId(),u.getEmail(),u.getDisplayName(),u.getRole(),u.isEnabled());}}
 @GetMapping @Transactional(readOnly=true) public Page<View> list(@AuthenticationPrincipal AppUser a,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return users.findByOrganizationId(a.getOrganizationId(),Checks.page(page,size)).map(View::from);}
 @PostMapping @Transactional public ResponseEntity<View> create(@AuthenticationPrincipal AppUser a,@Valid @RequestBody NewUser r){String role=Checks.oneOf(r.role(),"ADMIN","MANAGER","SALES","VIEWER");if(users.findByEmailIgnoreCase(r.email()).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Correo ya registrado");AppUser user=users.save(new AppUser(a.getOrganizationId(),r.email().trim(),encoder.encode(r.password()),r.displayName(),role));audit.log(a,"CREATED","USER",user.getId());return ResponseEntity.status(201).body(View.from(user));}
 @PutMapping("/{id}") @Transactional public View update(@AuthenticationPrincipal AppUser a,@PathVariable UUID id,@Valid @RequestBody UpdateUser r){AppUser u=find(a,id);String role=Checks.oneOf(r.role(),"ADMIN","MANAGER","SALES","VIEWER");if(u.getId().equals(a.getId()))Checks.require(r.enabled()&&"ADMIN".equals(role),"No puedes desactivar ni quitar tu propio rol administrador");u.changeDisplayName(r.displayName());u.changeRole(role);u.changeEnabled(r.enabled());audit.log(a,"UPDATED","USER",id);return View.from(u);}
 @PatchMapping("/{id}/password") @Transactional public ResponseEntity<Void> password(@AuthenticationPrincipal AppUser a,@PathVariable UUID id,@Valid @RequestBody PasswordChange r){AppUser u=find(a,id);u.changePassword(encoder.encode(r.password()));audit.log(a,"PASSWORD_RESET","USER",id);return ResponseEntity.noContent().build();}
 private AppUser find(AppUser a,UUID id){return users.findById(id).filter(u->u.getOrganizationId().equals(a.getOrganizationId())).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
}
