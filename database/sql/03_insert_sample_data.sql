SET search_path TO scm, public;

-- Sample warehouse
INSERT INTO scm.warehouses (warehouse_name, country)
VALUES
    ('London Distribution Centre', 'United Kingdom'),
    ('Singapore Hub', 'Singapore'),
    ('New York Warehouse', 'United States')
ON CONFLICT DO NOTHING;

-- Sample inventory items
INSERT INTO scm.inventory_items (item_code, item_name, category, unit_price)
VALUES
    ('ITM-001', 'Industrial Router', 'Electronics', 250.00),
    ('ITM-002', 'Medical Scanner Component', 'Healthcare', 980.50),
    ('ITM-003', 'Smart Sensor', 'Electronics', 75.25)
ON CONFLICT (item_code) DO NOTHING;

-- Sample vendor
INSERT INTO scm.vendors (vendor_name, country, contact_email, compliance_status, performance_score)
VALUES
    ('AsiaParts Ltd', 'Singapore', 'contact@asiaparts.example.com', 'APPROVED', 88.50),
    ('EuroSupply GmbH', 'Germany', 'info@eurosupply.example.com', 'APPROVED', 91.20)
ON CONFLICT DO NOTHING;
