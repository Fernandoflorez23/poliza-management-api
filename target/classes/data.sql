INSERT INTO polizas (id, numero_poliza, tipo, estado, canon, prima) VALUES
 (1, 'POL-IND-001', 'INDIVIDUAL', 'ACTIVA', 100.00, 500.00),
 (2, 'POL-COL-001', 'COLECTIVA', 'ACTIVA', 300.00, 1500.00),
 (3, 'POL-IND-002', 'INDIVIDUAL', 'CANCELADA', 120.00, 600.00);

INSERT INTO riesgos (id, descripcion, estado, poliza_id) VALUES
 (1, 'Riesgo unico del tomador', 'ACTIVO', 1),
 (2, 'Riesgo empleado A', 'ACTIVO', 2),
 (3, 'Riesgo empleado B', 'ACTIVO', 2);

ALTER TABLE polizas ALTER COLUMN id RESTART WITH 4;
ALTER TABLE riesgos ALTER COLUMN id RESTART WITH 4;
