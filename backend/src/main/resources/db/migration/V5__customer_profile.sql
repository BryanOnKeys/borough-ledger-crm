ALTER TABLE customers ADD COLUMN commercial_name VARCHAR(180),ADD COLUMN tax_id VARCHAR(30),ADD COLUMN country VARCHAR(100),ADD COLUMN address VARCHAR(300),ADD COLUMN website VARCHAR(220),ADD COLUMN description VARCHAR(2000),ADD COLUMN owner_id VARCHAR(36);
ALTER TABLE customers ADD CONSTRAINT fk_customer_owner FOREIGN KEY(owner_id) REFERENCES app_users(id);
CREATE INDEX idx_customer_tax ON customers(organization_id,tax_id);
