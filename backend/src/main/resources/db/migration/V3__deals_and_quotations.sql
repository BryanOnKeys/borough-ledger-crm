CREATE TABLE pipeline_stages (
 id VARCHAR(36) PRIMARY KEY,organization_id VARCHAR(36) NOT NULL,pipeline_id VARCHAR(36) NOT NULL,
 name VARCHAR(100) NOT NULL,position_index INT NOT NULL,outcome VARCHAR(15) NOT NULL,
 created_at DATETIME(6) NOT NULL, updated_at DATETIME(6) NOT NULL,archived_at DATETIME(6),version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT fk_stage_org FOREIGN KEY(organization_id) REFERENCES organizations(id),
 CONSTRAINT fk_stage_pipeline FOREIGN KEY(pipeline_id) REFERENCES pipelines(id),
 INDEX idx_stage_pipeline(organization_id,pipeline_id,position_index)
);
CREATE TABLE opportunities (
 id VARCHAR(36) PRIMARY KEY, organization_id VARCHAR(36) NOT NULL, title VARCHAR(180) NOT NULL,
 customer_id VARCHAR(36) NOT NULL,contact_id VARCHAR(36),owner_id VARCHAR(36),pipeline_id VARCHAR(36) NOT NULL,stage_id VARCHAR(36) NOT NULL,
 amount DECIMAL(17,2) NOT NULL,currency CHAR(3) NOT NULL,status VARCHAR(15) NOT NULL,
 expected_close DATE,closed_at DATE,loss_reason VARCHAR(500),description VARCHAR(2000),
 created_at DATETIME(6) NOT NULL,updated_at DATETIME(6) NOT NULL,archived_at DATETIME(6),version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT fk_opp_org FOREIGN KEY(organization_id) REFERENCES organizations(id),
 CONSTRAINT fk_opp_customer FOREIGN KEY(customer_id) REFERENCES customers(id),
 CONSTRAINT fk_opp_pipeline FOREIGN KEY(pipeline_id) REFERENCES pipelines(id),
 CONSTRAINT fk_opp_stage FOREIGN KEY(stage_id) REFERENCES pipeline_stages(id),
 INDEX idx_opp_org_status(organization_id,archived_at,status,created_at),
 INDEX idx_opp_stage(organization_id,stage_id)
);
CREATE TABLE opportunity_stage_history (
 id VARCHAR(36) PRIMARY KEY,organization_id VARCHAR(36) NOT NULL,opportunity_id VARCHAR(36) NOT NULL,
 from_stage_id VARCHAR(36),to_stage_id VARCHAR(36) NOT NULL,actor_id VARCHAR(36) NOT NULL,changed_at DATETIME(6) NOT NULL,
 CONSTRAINT fk_hist_org FOREIGN KEY(organization_id) REFERENCES organizations(id),
 CONSTRAINT fk_hist_opp FOREIGN KEY(opportunity_id) REFERENCES opportunities(id),
 CONSTRAINT fk_hist_actor FOREIGN KEY(actor_id) REFERENCES app_users(id),
 INDEX idx_hist_opp(organization_id,opportunity_id,changed_at)
);

CREATE TABLE quotations (
 id VARCHAR(36) PRIMARY KEY, organization_id VARCHAR(36) NOT NULL,number VARCHAR(45) NOT NULL UNIQUE,
 opportunity_id VARCHAR(36) NOT NULL,status VARCHAR(15) NOT NULL,currency CHAR(3) NOT NULL,valid_until DATE,
 subtotal DECIMAL(17,2) NOT NULL, discount_total DECIMAL(17,2) NOT NULL,tax_total DECIMAL(17,2) NOT NULL,total DECIMAL(17,2) NOT NULL,
 internal_notes VARCHAR(2000),created_at DATETIME(6) NOT NULL,updated_at DATETIME(6) NOT NULL,archived_at DATETIME(6),version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT fk_quote_org FOREIGN KEY(organization_id) REFERENCES organizations(id),
 CONSTRAINT fk_quote_opp FOREIGN KEY(opportunity_id) REFERENCES opportunities(id),
 INDEX idx_quote_org(organization_id,archived_at,status)
);
CREATE TABLE quotation_items (
 id VARCHAR(36) PRIMARY KEY,quotation_id VARCHAR(36) NOT NULL,product_id VARCHAR(36) NOT NULL,
 product_name VARCHAR(180) NOT NULL,sku_snapshot VARCHAR(50) NOT NULL,unit_price DECIMAL(17,2) NOT NULL,
 quantity DECIMAL(15,3) NOT NULL,discount_percent DECIMAL(5,2) NOT NULL,tax_percent DECIMAL(5,2) NOT NULL,
 line_total DECIMAL(17,2) NOT NULL,
 CONSTRAINT fk_quote_item_quote FOREIGN KEY(quotation_id) REFERENCES quotations(id),
 CONSTRAINT fk_quote_item_product FOREIGN KEY(product_id) REFERENCES products(id),
 INDEX idx_quote_items(quotation_id)
);
