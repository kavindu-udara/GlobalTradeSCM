-- =====================================================
-- GlobalTrade Logistics Corporation
-- Supply Chain Management System
-- PostgreSQL Schema
-- =====================================================

CREATE SCHEMA IF NOT EXISTS scm;

SET search_path TO scm, public;

-- =====================================================
-- Updated timestamp function
-- =====================================================

CREATE OR REPLACE FUNCTION scm.set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- Users and Roles
-- =====================================================

CREATE TABLE IF NOT EXISTS scm.users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS scm.roles (
    role_id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS scm.user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES scm.users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES scm.roles(role_id)
        ON DELETE CASCADE
);

-- =====================================================
-- Vendor Management
-- =====================================================

CREATE TABLE IF NOT EXISTS scm.vendors (
    vendor_id BIGSERIAL PRIMARY KEY,
    vendor_name VARCHAR(120) NOT NULL,
    country VARCHAR(80) NOT NULL,
    contact_email VARCHAR(120),
    compliance_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    performance_score NUMERIC(5,2) NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT chk_vendor_compliance_status
        CHECK (compliance_status IN ('PENDING', 'APPROVED', 'REJECTED', 'UNDER_REVIEW')),
    CONSTRAINT chk_vendor_performance_score
        CHECK (performance_score BETWEEN 0 AND 100)
);

-- =====================================================
-- Warehouses and Inventory
-- =====================================================

CREATE TABLE IF NOT EXISTS scm.warehouses (
    warehouse_id BIGSERIAL PRIMARY KEY,
    warehouse_name VARCHAR(120) NOT NULL,
    country VARCHAR(80) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS scm.inventory_items (
    item_id BIGSERIAL PRIMARY KEY,
    item_code VARCHAR(50) NOT NULL UNIQUE,
    item_name VARCHAR(150) NOT NULL,
    category VARCHAR(80),
    unit_price NUMERIC(12,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_item_unit_price
        CHECK (unit_price >= 0)
);

CREATE TABLE IF NOT EXISTS scm.inventory_levels (
    inventory_level_id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity_available INTEGER NOT NULL DEFAULT 0,
    reorder_level INTEGER NOT NULL DEFAULT 0,
    last_replenished_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_inventory_levels_item
        FOREIGN KEY (item_id)
        REFERENCES scm.inventory_items(item_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_inventory_levels_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES scm.warehouses(warehouse_id)
        ON DELETE CASCADE,
    CONSTRAINT uq_inventory_item_warehouse
        UNIQUE (item_id, warehouse_id),
    CONSTRAINT chk_quantity_available
        CHECK (quantity_available >= 0),
    CONSTRAINT chk_reorder_level
        CHECK (reorder_level >= 0)
);

-- =====================================================
-- Shipment Management
-- =====================================================

CREATE TABLE IF NOT EXISTS scm.shipments (
    shipment_id BIGSERIAL PRIMARY KEY,
    tracking_number VARCHAR(80) NOT NULL UNIQUE,
    vendor_id BIGINT,
    origin VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
    expected_delivery_date TIMESTAMPTZ,
    actual_delivery_date TIMESTAMPTZ,
    carrier_name VARCHAR(120),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_shipments_vendor
        FOREIGN KEY (vendor_id)
        REFERENCES scm.vendors(vendor_id)
        ON DELETE SET NULL,
    CONSTRAINT chk_shipment_status
        CHECK (status IN (
            'CREATED',
            'IN_TRANSIT',
            'CUSTOMS_HOLD',
            'DELAYED',
            'DELIVERED',
            'CANCELLED'
        ))
);

CREATE TABLE IF NOT EXISTS scm.shipment_events (
    event_id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_description VARCHAR(255),
    location VARCHAR(150),
    event_timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_shipment_events_shipment
        FOREIGN KEY (shipment_id)
        REFERENCES scm.shipments(shipment_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_shipment_event_type
        CHECK (event_type IN (
            'CREATED',
            'PICKED_UP',
            'IN_TRANSIT',
            'CUSTOMS_HOLD',
            'DELAYED',
            'OUT_FOR_DELIVERY',
            'DELIVERED',
            'EXCEPTION'
        ))
);

-- =====================================================
-- Customs Compliance
-- =====================================================

CREATE TABLE IF NOT EXISTS scm.customs_documents (
    document_id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL,
    document_type VARCHAR(80) NOT NULL,
    submission_deadline TIMESTAMPTZ NOT NULL,
    approval_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMPTZ,
    approved_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_customs_documents_shipment
        FOREIGN KEY (shipment_id)
        REFERENCES scm.shipments(shipment_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_customs_approval_status
        CHECK (approval_status IN (
            'PENDING',
            'SUBMITTED',
            'APPROVED',
            'REJECTED',
            'EXPIRED'
        ))
);

-- =====================================================
-- Route Optimization
-- =====================================================

CREATE TABLE IF NOT EXISTS scm.route_plans (
    route_plan_id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL,
    route_details TEXT NOT NULL,
    estimated_cost NUMERIC(14,2) NOT NULL DEFAULT 0,
    estimated_duration_minutes INTEGER NOT NULL DEFAULT 0,
    optimized_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_route_plans_shipment
        FOREIGN KEY (shipment_id)
        REFERENCES scm.shipments(shipment_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_route_estimated_cost
        CHECK (estimated_cost >= 0),
    CONSTRAINT chk_route_duration
        CHECK (estimated_duration_minutes >= 0)
);

-- =====================================================
-- Alerts and Monitoring
-- =====================================================

CREATE TABLE IF NOT EXISTS scm.alerts (
    alert_id BIGSERIAL PRIMARY KEY,
    alert_type VARCHAR(60) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    related_entity_type VARCHAR(60),
    related_entity_id BIGINT,
    message VARCHAR(500) NOT NULL,
    acknowledged BOOLEAN NOT NULL DEFAULT FALSE,
    acknowledged_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_alert_severity
        CHECK (severity IN ('INFO', 'WARNING', 'CRITICAL')),
    CONSTRAINT chk_alert_type
        CHECK (alert_type IN (
            'SHIPMENT_DELAY',
            'INVENTORY_SHORTAGE',
            'VENDOR_PERFORMANCE',
            'CUSTOMS_DEADLINE',
            'SYSTEM_ERROR'
        ))
);

-- =====================================================
-- Audit Log
-- =====================================================

CREATE TABLE IF NOT EXISTS scm.audit_logs (
    audit_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(80) NOT NULL,
    action VARCHAR(120) NOT NULL,
    module_name VARCHAR(80) NOT NULL,
    entity_type VARCHAR(80),
    entity_id BIGINT,
    ip_address VARCHAR(50),
    outcome VARCHAR(20) NOT NULL,
    details JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_audit_outcome
        CHECK (outcome IN ('SUCCESS', 'FAILURE'))
);

-- =====================================================
-- Integration Outbox for external system resilience
-- =====================================================

CREATE TABLE IF NOT EXISTS scm.integration_outbox (
    outbox_id BIGSERIAL PRIMARY KEY,
    aggregate_type VARCHAR(80) NOT NULL,
    aggregate_id BIGINT NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    retry_count INTEGER NOT NULL DEFAULT 0,
    error_message VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    processed_at TIMESTAMPTZ,
    CONSTRAINT chk_outbox_status
        CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'))
);

-- =====================================================
-- Indexes
-- =====================================================

CREATE INDEX IF NOT EXISTS idx_users_username
    ON scm.users(username);

CREATE INDEX IF NOT EXISTS idx_roles_role_name
    ON scm.roles(role_name);

CREATE INDEX IF NOT EXISTS idx_shipments_status
    ON scm.shipments(status);

CREATE INDEX IF NOT EXISTS idx_shipments_expected_delivery
    ON scm.shipments(expected_delivery_date);

CREATE INDEX IF NOT EXISTS idx_shipment_events_shipment
    ON scm.shipment_events(shipment_id);

CREATE INDEX IF NOT EXISTS idx_inventory_levels_item
    ON scm.inventory_levels(item_id);

CREATE INDEX IF NOT EXISTS idx_inventory_levels_warehouse
    ON scm.inventory_levels(warehouse_id);

CREATE INDEX IF NOT EXISTS idx_customs_documents_deadline
    ON scm.customs_documents(submission_deadline);

CREATE INDEX IF NOT EXISTS idx_customs_documents_status
    ON scm.customs_documents(approval_status);

CREATE INDEX IF NOT EXISTS idx_alerts_created_at
    ON scm.alerts(created_at);

CREATE INDEX IF NOT EXISTS idx_alerts_acknowledged
    ON scm.alerts(acknowledged);

CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at
    ON scm.audit_logs(created_at);

CREATE INDEX IF NOT EXISTS idx_audit_logs_username
    ON scm.audit_logs(username);

CREATE INDEX IF NOT EXISTS idx_integration_outbox_status
    ON scm.integration_outbox(status);

-- =====================================================
-- Updated At Triggers
-- =====================================================

DROP TRIGGER IF EXISTS trg_users_updated_at ON scm.users;
CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON scm.users
FOR EACH ROW
EXECUTE FUNCTION scm.set_updated_at();

DROP TRIGGER IF EXISTS trg_vendors_updated_at ON scm.vendors;
CREATE TRIGGER trg_vendors_updated_at
BEFORE UPDATE ON scm.vendors
FOR EACH ROW
EXECUTE FUNCTION scm.set_updated_at();

DROP TRIGGER IF EXISTS trg_warehouses_updated_at ON scm.warehouses;
CREATE TRIGGER trg_warehouses_updated_at
BEFORE UPDATE ON scm.warehouses
FOR EACH ROW
EXECUTE FUNCTION scm.set_updated_at();

DROP TRIGGER IF EXISTS trg_inventory_items_updated_at ON scm.inventory_items;
CREATE TRIGGER trg_inventory_items_updated_at
BEFORE UPDATE ON scm.inventory_items
FOR EACH ROW
EXECUTE FUNCTION scm.set_updated_at();

DROP TRIGGER IF EXISTS trg_inventory_levels_updated_at ON scm.inventory_levels;
CREATE TRIGGER trg_inventory_levels_updated_at
BEFORE UPDATE ON scm.inventory_levels
FOR EACH ROW
EXECUTE FUNCTION scm.set_updated_at();

DROP TRIGGER IF EXISTS trg_shipments_updated_at ON scm.shipments;
CREATE TRIGGER trg_shipments_updated_at
BEFORE UPDATE ON scm.shipments
FOR EACH ROW
EXECUTE FUNCTION scm.set_updated_at();

DROP TRIGGER IF EXISTS trg_customs_documents_updated_at ON scm.customs_documents;
CREATE TRIGGER trg_customs_documents_updated_at
BEFORE UPDATE ON scm.customs_documents
FOR EACH ROW
EXECUTE FUNCTION scm.set_updated_at();
