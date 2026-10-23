CREATE TABLE organizations (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    created_at DATETIME(6) NOT NULL
);

CREATE TABLE app_users (
    id VARCHAR(36) PRIMARY KEY,
    organization_id VARCHAR(36) NOT NULL,
    email VARCHAR(190) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_users_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    INDEX idx_users_org (organization_id)
);

CREATE TABLE customers (
    id VARCHAR(36) PRIMARY KEY,
    organization_id VARCHAR(36) NOT NULL,
    name VARCHAR(180) NOT NULL,
    email VARCHAR(190),
    phone VARCHAR(40),
    industry VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    archived_at DATETIME(6),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_customers_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    INDEX idx_customers_org_active (organization_id, archived_at, name),
    INDEX idx_customers_email (organization_id, email)
);

CREATE TABLE audit_logs (
    id VARCHAR(36) PRIMARY KEY,
    organization_id VARCHAR(36) NOT NULL,
    actor_user_id VARCHAR(36) NOT NULL,
    action VARCHAR(40) NOT NULL,
    entity_type VARCHAR(40) NOT NULL,
    entity_id VARCHAR(36) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_audit_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_audit_actor FOREIGN KEY (actor_user_id) REFERENCES app_users(id),
    INDEX idx_audit_org_when (organization_id, created_at)
);
