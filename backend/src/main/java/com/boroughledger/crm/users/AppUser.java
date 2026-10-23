package com.boroughledger.crm.users;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity @Table(name = "app_users")
public class AppUser implements UserDetails {
    @Id @JdbcTypeCode(SqlTypes.VARCHAR) @Column(length = 36, nullable = false)
    private UUID id;
    @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name = "organization_id", nullable = false, length = 36)
    private UUID organizationId;
    @Column(nullable = false, length = 190, unique = true) private String email;
    @Column(name = "password_hash", nullable = false, length = 100) private String passwordHash;
    @Column(name = "display_name", nullable = false, length = 120) private String displayName;
    @Column(nullable = false, length = 20) private String role;
    @Column(nullable = false) private boolean enabled;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    protected AppUser() { }
    public AppUser(UUID organizationId, String email, String passwordHash, String displayName, String role) {
        this.id = UUID.randomUUID(); this.organizationId = organizationId; this.email = email.toLowerCase();
        this.passwordHash = passwordHash; this.displayName = displayName; this.role = role;
        this.enabled = true; this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
    }
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return passwordHash; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
    public void changeRole(String role){this.role=role;}
    public void changeDisplayName(String name){this.displayName=name;}
    public void changePassword(String hash){this.passwordHash=hash;}
    public void changeEnabled(boolean state){this.enabled=state;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    @Override public boolean isEnabled() { return enabled; }
}
