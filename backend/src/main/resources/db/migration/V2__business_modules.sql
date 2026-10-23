CREATE TABLE contacts (
  id VARCHAR(36) PRIMARY KEY,
  organization_id VARCHAR(36) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  email VARCHAR(190),
  phone VARCHAR(40),
  job_title VARCHAR(130),
  customer_id VARCHAR(36),
  owner_id VARCHAR(36),
  notes VARCHAR(2000),
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  archived_at DATETIME(6),
  version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_contacts_org FOREIGN KEY(organization_id) REFERENCES organizations(id),
  INDEX idx_contacts_org_active(organization_id, archived_at, created_at)
);

CREATE TABLE leads (
  id VARCHAR(36) PRIMARY KEY,
  organization_id VARCHAR(36) NOT NULL,
  name VARCHAR(180) NOT NULL,
  email VARCHAR(190),
  phone VARCHAR(40),
  company VARCHAR(180),
  source VARCHAR(80),
  status VARCHAR(30) NOT NULL,
  owner_id VARCHAR(36),
  expected_value DECIMAL(17,2),
  next_contact_at DATETIME(6),
  notes VARCHAR(2000),
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  archived_at DATETIME(6),
  version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_leads_org FOREIGN KEY(organization_id) REFERENCES organizations(id),
  INDEX idx_leads_org_active(organization_id, archived_at, created_at)
);

CREATE TABLE pipelines (
  id VARCHAR(36) PRIMARY KEY,
  organization_id VARCHAR(36) NOT NULL,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(500),
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  archived_at DATETIME(6),
  version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_pipelines_org FOREIGN KEY(organization_id) REFERENCES organizations(id),
  INDEX idx_pipelines_org_active(organization_id, archived_at, created_at)
);

CREATE TABLE activities (
  id VARCHAR(36) PRIMARY KEY,
  organization_id VARCHAR(36) NOT NULL,
  title VARCHAR(180) NOT NULL,
  kind VARCHAR(30) NOT NULL,
  due_at DATETIME(6),
  completed_at DATETIME(6),
  customer_id VARCHAR(36),
  contact_id VARCHAR(36),
  opportunity_id VARCHAR(36),
  owner_id VARCHAR(36),
  details VARCHAR(2000),
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  archived_at DATETIME(6),
  version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_activities_org FOREIGN KEY(organization_id) REFERENCES organizations(id),
  INDEX idx_activities_org_active(organization_id, archived_at, created_at)
);

CREATE TABLE crm_tasks (
  id VARCHAR(36) PRIMARY KEY,
  organization_id VARCHAR(36) NOT NULL,
  title VARCHAR(180) NOT NULL,
  description VARCHAR(2000),
  status VARCHAR(30) NOT NULL,
  priority VARCHAR(20) NOT NULL,
  due_at DATETIME(6),
  completed_at DATETIME(6),
  owner_id VARCHAR(36),
  opportunity_id VARCHAR(36),
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  archived_at DATETIME(6),
  version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_crm_tasks_org FOREIGN KEY(organization_id) REFERENCES organizations(id),
  INDEX idx_crm_tasks_org_active(organization_id, archived_at, created_at)
);

CREATE TABLE products (
  id VARCHAR(36) PRIMARY KEY,
  organization_id VARCHAR(36) NOT NULL,
  sku VARCHAR(50) NOT NULL,
  name VARCHAR(180) NOT NULL,
  description VARCHAR(2000),
  unit_price DECIMAL(17,2) NOT NULL,
  currency CHAR(3) NOT NULL,
  kind VARCHAR(20) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  archived_at DATETIME(6),
  version BIGINT NOT NULL DEFAULT 0,
  CONSTRAINT fk_products_org FOREIGN KEY(organization_id) REFERENCES organizations(id),
  INDEX idx_products_org_active(organization_id, archived_at, created_at)
);
