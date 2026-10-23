package com.boroughledger.crm.organizations;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name = "organizations")
public class Organization {
    @Id @JdbcTypeCode(SqlTypes.VARCHAR) @Column(length = 36, nullable = false)
    private UUID id;
    @Column(nullable = false, length = 160) private String name;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    protected Organization() { }
    public Organization(String name) {
        this.id = UUID.randomUUID(); this.name = name; this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
    }
    public UUID getId() { return id; }
    public String getName(){return name;}
    public void rename(String name){this.name=name;}
}
