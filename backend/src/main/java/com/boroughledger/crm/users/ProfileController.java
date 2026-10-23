package com.boroughledger.crm.users;
import com.boroughledger.crm.shared.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
@RestController @RequestMapping("/api/v1/profile") public class ProfileController {
 private final UserRepository users;private final PasswordEncoder encoder;private final AuditService audit;
 public ProfileController(UserRepository users,PasswordEncoder encoder,AuditService audit){this.users=users;this.encoder=encoder;this.audit=audit;}
 public record ChangePassword(@NotBlank String currentPassword,@NotBlank @Size(min=12,max=100) String newPassword){}
 @PostMapping("/password") @Transactional public void password(@AuthenticationPrincipal AppUser actor,@Valid @RequestBody ChangePassword r){AppUser user=users.findById(actor.getId()).orElseThrow();if(!encoder.matches(r.currentPassword(),user.getPassword()))throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,"Contraseña actual incorrecta");user.changePassword(encoder.encode(r.newPassword()));audit.log(actor,"PASSWORD_CHANGED","USER",user.getId());}
}
