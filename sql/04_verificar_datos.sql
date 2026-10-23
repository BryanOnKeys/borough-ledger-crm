-- BOROUGH & LEDGER CRM | VALIDACION DE BASE
USE borough_ledger;
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;
SELECT 'Clientes' modulo, COUNT(*) registros FROM customers WHERE archived_at IS NULL
UNION ALL SELECT 'Contactos', COUNT(*) FROM contacts WHERE archived_at IS NULL
UNION ALL SELECT 'Prospectos', COUNT(*) FROM leads WHERE archived_at IS NULL
UNION ALL SELECT 'Embudos', COUNT(*) FROM pipelines WHERE archived_at IS NULL
UNION ALL SELECT 'Etapas', COUNT(*) FROM pipeline_stages WHERE archived_at IS NULL
UNION ALL SELECT 'Oportunidades', COUNT(*) FROM opportunities WHERE archived_at IS NULL
UNION ALL SELECT 'Actividades', COUNT(*) FROM activities WHERE archived_at IS NULL
UNION ALL SELECT 'Tareas', COUNT(*) FROM crm_tasks WHERE archived_at IS NULL
UNION ALL SELECT 'Productos', COUNT(*) FROM products WHERE archived_at IS NULL
UNION ALL SELECT 'Cotizaciones', COUNT(*) FROM quotations WHERE archived_at IS NULL
UNION ALL SELECT 'Partidas', COUNT(*) FROM quotation_items
UNION ALL SELECT 'Historial de etapas', COUNT(*) FROM opportunity_stage_history;

-- Validación de cálculos financieros: ninguna cotización debería aparecer.
SELECT q.number, q.subtotal, q.discount_total, q.tax_total, q.total,
 COALESCE(SUM(i.line_total),0) AS suma_partidas
FROM quotations q LEFT JOIN quotation_items i ON i.quotation_id=q.id
WHERE q.number LIKE 'COT-2026-COM-%'
GROUP BY q.id,q.number,q.subtotal,q.discount_total,q.tax_total,q.total
HAVING ABS(q.total-COALESCE(SUM(i.line_total),0))>0.009;

-- Comprobar registros de oportunidades con clientes y etapas.
SELECT o.title, c.name AS cliente, p.name AS embudo,
 s.name AS etapa, o.status, o.amount, o.currency
FROM opportunities o
JOIN customers c ON c.id=o.customer_id
JOIN pipelines p ON p.id=o.pipeline_id
JOIN pipeline_stages s ON s.id=o.stage_id
WHERE o.archived_at IS NULL
ORDER BY o.created_at DESC LIMIT 30;
