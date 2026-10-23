package com.boroughledger.crm.auth;

import com.boroughledger.crm.users.AppUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository contextRepository;
    private final com.boroughledger.crm.security.LoginAttemptGuard attempts;
    private final com.boroughledger.crm.shared.AuditService audit;
    public AuthController(AuthenticationManager authenticationManager, SecurityContextRepository contextRepository, com.boroughledger.crm.security.LoginAttemptGuard attempts, com.boroughledger.crm.shared.AuditService audit) {
        this.authenticationManager = authenticationManager;
        this.contextRepository = contextRepository;
        this.attempts = attempts;
        this.audit = audit;
    }
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) { }
    public record UserResponse(String email, String displayName, String role) {
        static UserResponse from(AppUser u) { return new UserResponse(u.getEmail(), u.getDisplayName(), u.getRole()); }
    }
    public record CsrfResponse(String token, String headerName) { }

    @GetMapping("/csrf")
    public CsrfResponse csrf(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return new CsrfResponse(token.getToken(), token.getHeaderName());
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest login, HttpServletRequest request, HttpServletResponse response) {
        String ip=request.getRemoteAddr();
        attempts.verify(ip);
        Authentication auth;
        try { auth = authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(login.email(), login.password())); }
        catch(org.springframework.security.core.AuthenticationException e){ attempts.fail(ip);throw e; }
        attempts.reset(ip);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        request.getSession(true);
        request.changeSessionId();
        SecurityContextHolder.setContext(context);
        contextRepository.saveContext(context, request, response);
        AppUser actor=(AppUser) auth.getPrincipal();
        audit.log(actor,"LOGIN","USER",actor.getId());
        return UserResponse.from(actor);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication auth) { return UserResponse.from((AppUser) auth.getPrincipal()); }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if(authentication!=null && authentication.getPrincipal() instanceof AppUser actor) audit.log(actor,"LOGOUT","USER",actor.getId());
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return ResponseEntity.noContent().build();
    }
}
