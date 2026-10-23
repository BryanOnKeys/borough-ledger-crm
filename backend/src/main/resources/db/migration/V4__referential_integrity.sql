-- Indices de referencia y claves relacionales para registros comerciales.
ALTER TABLE contacts ADD CONSTRAINT fk_contact_customer FOREIGN KEY(customer_id) REFERENCES customers(id);
ALTER TABLE contacts ADD CONSTRAINT fk_contact_owner FOREIGN KEY(owner_id) REFERENCES app_users(id);
ALTER TABLE leads ADD CONSTRAINT fk_lead_owner FOREIGN KEY(owner_id) REFERENCES app_users(id);
ALTER TABLE activities ADD CONSTRAINT fk_activity_customer FOREIGN KEY(customer_id) REFERENCES customers(id);
ALTER TABLE activities ADD CONSTRAINT fk_activity_contact FOREIGN KEY(contact_id) REFERENCES contacts(id);
ALTER TABLE activities ADD CONSTRAINT fk_activity_deal FOREIGN KEY(opportunity_id) REFERENCES opportunities(id);
ALTER TABLE activities ADD CONSTRAINT fk_activity_owner FOREIGN KEY(owner_id) REFERENCES app_users(id);
ALTER TABLE crm_tasks ADD CONSTRAINT fk_task_owner FOREIGN KEY(owner_id) REFERENCES app_users(id);
ALTER TABLE crm_tasks ADD CONSTRAINT fk_task_deal FOREIGN KEY(opportunity_id) REFERENCES opportunities(id);
ALTER TABLE opportunities ADD CONSTRAINT fk_opp_contact FOREIGN KEY(contact_id) REFERENCES contacts(id);
ALTER TABLE opportunities ADD CONSTRAINT fk_opp_owner FOREIGN KEY(owner_id) REFERENCES app_users(id);
CREATE UNIQUE INDEX uk_products_org_sku ON products(organization_id,sku);
CREATE INDEX idx_tasks_org_due ON crm_tasks(organization_id,archived_at,due_at,status);
CREATE INDEX idx_leads_org_status ON leads(organization_id,archived_at,status);
