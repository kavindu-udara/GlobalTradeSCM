SET search_path TO scm, public;

INSERT INTO scm.roles (role_name, description)
VALUES
    ('LOGISTICS_COORDINATOR', 'Manages shipments and logistics operations'),
    ('WAREHOUSE_MANAGER', 'Manages inventory and warehouse operations'),
    ('CUSTOMS_AGENT', 'Manages customs documentation and compliance'),
    ('VENDOR_REPRESENTATIVE', 'Views vendor performance and assigned data'),
    ('CUSTOMER', 'Tracks shipments'),
    ('SYSTEM_ADMIN', 'Manages users, roles and system configuration'),
    ('AUDITOR', 'Views audit logs and compliance reports')
ON CONFLICT (role_name) DO NOTHING;
