SET search_path TO scm, public;

-- =====================================================
-- 1. Users (Dummy password hashes for now)
-- =====================================================
INSERT INTO scm.users (username, password_hash, full_name, email, active) VALUES
('admin', 'dummy_hash_admin', 'System Administrator', 'admin@globaltrade.com', TRUE),
('jdoe', 'dummy_hash_jdoe', 'John Doe (Logistics)', 'jdoe@globaltrade.com', TRUE),
('asmith', 'dummy_hash_asmith', 'Alice Smith (Warehouse)', 'asmith@globaltrade.com', TRUE),
('vendor1', 'dummy_hash_vendor1', 'Vendor Rep 1', 'rep@asiaparts.com', TRUE),
('customs_agent', 'dummy_hash_customs', 'Bob Customs', 'bob@customs.gov', TRUE)
ON CONFLICT (username) DO NOTHING;

-- =====================================================
-- 2. Map Users to Roles
-- =====================================================
INSERT INTO scm.user_roles (user_id, role_id)
SELECT u.user_id, r.role_id FROM scm.users u, scm.roles r
WHERE (u.username = 'admin' AND r.role_name = 'SYSTEM_ADMIN')
   OR (u.username = 'jdoe' AND r.role_name = 'LOGISTICS_COORDINATOR')
   OR (u.username = 'asmith' AND r.role_name = 'WAREHOUSE_MANAGER')
   OR (u.username = 'vendor1' AND r.role_name = 'VENDOR_REPRESENTATIVE')
   OR (u.username = 'customs_agent' AND r.role_name = 'CUSTOMS_AGENT')
ON CONFLICT DO NOTHING;

-- =====================================================
-- 3. Vendors
-- =====================================================
INSERT INTO scm.vendors (vendor_name, country, contact_email, compliance_status, performance_score, active) VALUES
('AsiaParts Ltd', 'Singapore', 'contact@asiaparts.com', 'APPROVED', 92.5, TRUE),
('EuroSupply GmbH', 'Germany', 'info@eurosupply.de', 'APPROVED', 88.0, TRUE),
('GlobalMaterials Inc', 'USA', 'sales@globalmaterials.com', 'UNDER_REVIEW', 75.0, TRUE),
('Shenzhen Electronics', 'China', 'support@szelec.cn', 'PENDING', 60.0, TRUE),
('LatAm Logistics Partners', 'Brazil', 'ops@latamlog.br', 'REJECTED', 45.0, FALSE)
ON CONFLICT DO NOTHING;

-- =====================================================
-- 4. Warehouses
-- =====================================================
INSERT INTO scm.warehouses (warehouse_name, country, active) VALUES
('Singapore Mega Hub', 'Singapore', TRUE),
('Frankfurt Distribution Center', 'Germany', TRUE),
('New York Port Authority Warehouse', 'USA', TRUE),
('London Heathrow Cargo', 'United Kingdom', TRUE)
ON CONFLICT DO NOTHING;

-- =====================================================
-- 5. Inventory Items
-- =====================================================
INSERT INTO scm.inventory_items (item_code, item_name, category, unit_price) VALUES
('ITM-001', 'Industrial Router', 'Electronics', 250.00),
('ITM-002', 'Medical Scanner Component', 'Healthcare', 980.50),
('ITM-003', 'Smart Sensor v2', 'Electronics', 75.25),
('ITM-004', 'Heavy Duty Battery Pack', 'Automotive', 120.00),
('ITM-005', 'Precision Valve', 'Manufacturing', 45.99)
ON CONFLICT (item_code) DO NOTHING;

-- =====================================================
-- 6. Inventory Levels
-- (Notice some are BELOW reorder_level to trigger your Timer alerts!)
-- =====================================================
INSERT INTO scm.inventory_levels (item_id, warehouse_id, quantity_available, reorder_level, version) VALUES
(1, 1, 50, 20, 0),  -- OK
(1, 2, 5, 20, 0),   -- LOW STOCK (Triggers alert)
(2, 1, 100, 10, 0), -- OK
(2, 3, 2, 15, 0),   -- LOW STOCK (Triggers alert)
(3, 1, 500, 100, 0),-- OK
(4, 2, 10, 50, 0),  -- LOW STOCK (Triggers alert)
(5, 3, 200, 50, 0)  -- OK
ON CONFLICT (item_id, warehouse_id) DO NOTHING;

-- =====================================================
-- 7. Shipments
-- =====================================================
INSERT INTO scm.shipments (tracking_number, vendor_id, origin, destination, status, expected_delivery_date, carrier_name, version) VALUES
('TRK-1001', 1, 'Singapore', 'New York', 'IN_TRANSIT', NOW() + INTERVAL '5 days', 'Maersk Line', 0),
('TRK-1002', 2, 'Frankfurt', 'London', 'DELAYED', NOW() - INTERVAL '1 day', 'Lufthansa Cargo', 0),
('TRK-1003', 3, 'Los Angeles', 'Tokyo', 'CUSTOMS_HOLD', NOW() + INTERVAL '2 days', 'FedEx', 0),
('TRK-1004', 1, 'Singapore', 'Sydney', 'DELIVERED', NOW() - INTERVAL '5 days', 'DHL Express', 0),
('TRK-1005', 4, 'Shenzhen', 'Los Angeles', 'CREATED', NOW() + INTERVAL '10 days', 'COSCO Shipping', 0)
ON CONFLICT (tracking_number) DO NOTHING;

-- =====================================================
-- 8. Customs Documents (Upcoming deadlines for Timers)
-- =====================================================
INSERT INTO scm.customs_documents (shipment_id, document_type, submission_deadline, approval_status, version)
SELECT s.shipment_id, 'COMMERCIAL_INVOICE', NOW() + INTERVAL '12 hours', 'PENDING', 0
FROM scm.shipments s WHERE s.tracking_number = 'TRK-1001'
ON CONFLICT DO NOTHING;

INSERT INTO scm.customs_documents (shipment_id, document_type, submission_deadline, approval_status, version)
SELECT s.shipment_id, 'BILL_OF_LADING', NOW() - INTERVAL '2 hours', 'SUBMITTED', 0
FROM scm.shipments s WHERE s.tracking_number = 'TRK-1003'
ON CONFLICT DO NOTHING;

-- =====================================================
-- 9. Alerts & Audit Logs (Pre-seeded for testing)
-- =====================================================
INSERT INTO scm.alerts (alert_type, severity, related_entity_type, related_entity_id, message, acknowledged) VALUES
('SHIPMENT_DELAY', 'WARNING', 'SHIPMENT', 2, 'Shipment TRK-1002 is delayed due to weather conditions.', FALSE),
('VENDOR_PERFORMANCE', 'INFO', 'VENDOR', 4, 'Vendor Shenzhen Electronics performance score dropped below 70.', FALSE);

INSERT INTO scm.audit_logs (username, action, module_name, entity_type, entity_id, outcome, details) VALUES
('admin', 'CREATE_USER', 'UserService', 'USER', 2, 'SUCCESS', '{"new_user": "jdoe"}'),
('jdoe', 'UPDATE_SHIPMENT', 'ShipmentService', 'SHIPMENT', 1, 'SUCCESS', '{"status": "IN_TRANSIT"}');