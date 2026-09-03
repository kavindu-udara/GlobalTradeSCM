# GlobalTrade Logistics – Supply Chain Management System (SCMS)

> **Business Component Development II | Java Institute of Advanced Technology**  
> Enterprise Jakarta EE Supply Chain Modernization Platform

## 📋 Project Overview

GlobalTrade Logistics Corporation required a modernization of their legacy monolithic supply chain platform. This project delivers a **modular, enterprise-grade Jakarta EE 10 application** that automates logistics operations, enforces international trade compliance, and provides 99.9% availability through advanced resilience patterns.

The system demonstrates mastery of Enterprise JavaBeans (EJB), container-managed transactions, interceptor-driven AOP, WildFly Elytron security, and event-driven async processing — all deployed via a professional multi-module EAR architecture.

---

## 🏗️ Architecture

```
Client / REST Consumer
        │
┌───────▼────────┐
│  JAX-RS Web    │  ← REST API + Programmatic Security
│  Module (WAR)  │
└───────┬────────┘
        │
┌───────▼────────┐
│  EJB Business  │  ← Stateless/Singleton Beans + BMT/CMT
│  Services      │
└───────┬────────┘
        │
┌───────▼────────┐
│  Interceptor   │  ← Audit, Performance, Security AOP
│  Chain         │
└───────┬────────┘
        │
┌───────▼────────┐
│  JPA/Hibernate │  ← Transactional Persistence + Locking
│  Persistence   │
└───────┬────────┘
        │
┌───────▼────────┐
│  PostgreSQL    │  ← SCM Schema + Outbox Table
│  Database      │
└────────────────┘
```

### Multi-Module Structure (Split Directory)

| Module | Packaging | Purpose |
|--------|-----------|---------|
| `scms-common` | JAR | Shared exceptions, constants, utilities |
| `scms-ejb-api` | JAR | Local interfaces, custom interceptor bindings |
| `scms-ejb-core` | EJB JAR | Business logic, entities, timers, interceptors |
| `scms-web` | WAR | JAX-RS endpoints, web security constraints |
| `scms-ear` | EAR | Enterprise archive assembly & deployment |

---

## ⚙️ Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Platform | Jakarta EE | 10 |
| Application Server | WildFly | 40.x |
| Database | PostgreSQL | 15+ |
| ORM | Hibernate / JPA | 7.x |
| Security | WildFly Elytron (JDBC Realm) | — |
| Build Tool | Maven (Multi-Module) | 3.9+ |
| Testing | JUnit 5 + Mockito | 5.10 / 5.11 |
| Language | Java | 17 LTS |

---

## 🚀 Key Enterprise Features

### Timer Services
- **Declarative (`@Schedule`):** Inventory monitoring every 30 seconds with automatic shortage alerts
- **Programmatic (`TimerService`):** Dynamic customs deadline tracking per shipment with `persistent=true` for crash recovery
- **Recovery Timer:** Polls `integration_outbox` for failed carrier dispatches and retries automatically

### Interceptor Framework (AOP)
- **Custom `@LogisticsAudit` annotation** with runtime metadata reading via reflection
- **Performance monitoring** with execution time tracking
- **Security interceptor** with programmatic vendor suspension checks
- **`REQUIRES_NEW` audit isolation** ensuring compliance logs survive transaction rollbacks

### Transaction Management
- **CMT (`REQUIRED`)** for standard CRUD with pessimistic locking (`PESSIMISTIC_WRITE`) on inventory
- **BMT (`UserTransaction`)** for customs clearance to avoid holding DB connections during external API calls
- **`@ApplicationException(rollback=true)`** for controlled business-rule rollbacks

### Security (WildFly Elytron)
- Database-backed JDBC Realm with role mapping from PostgreSQL
- Multi-layered: `web.xml` constraints (HTTP 401) + `@RolesAllowed` (EJB) + Programmatic `SessionContext`
- Roles: `SYSTEM_ADMIN`, `LOGISTICS_COORDINATOR`, `WAREHOUSE_MANAGER`, `CUSTOMS_AGENT`, `VENDOR_REPRESENTATIVE`

### Exception Handling & Resilience
- **Transactional Outbox Pattern:** Failed carrier API calls persisted to `integration_outbox` in `REQUIRES_NEW` transaction, retried by background timer
- **`@Asynchronous` EJB:** Non-blocking vendor notification emails
- **JAX-RS `ExceptionMapper`:** Clean HTTP 503 responses for carrier failures

---

## 🛠️ Setup & Deployment

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL 15+
- WildFly 40.x

### Database Setup
```bash
psql -U postgres
CREATE USER scms_user WITH PASSWORD 'Scms@12345';
CREATE DATABASE globaltrade_scm OWNER scms_user;
GRANT ALL PRIVILEGES ON DATABASE globaltrade_scm TO scms_user;
\q

psql -U scms_user -d globaltrade_scm -f database/sql/01_create_schema.sql
psql -U scms_user -d globaltrade_scm -f database/sql/02_insert_roles.sql
psql -U scms_user -d globaltrade_scm -f database/sql/04_insert_dummy_data.sql
```

### WildFly Configuration
```bash
# Start WildFly CLI
./bin/jboss-cli.sh --connect

# Add PostgreSQL driver module
module add --name=org.postgresql \
  --resources=/path/to/postgresql-42.7.11.jar \
  --dependencies=jakarta.api,jakarta.transaction.api

# Configure datasource & security (see config/ directory for full CLI scripts)
```

### Build & Deploy
```bash
mvn clean install
cp app/scms-ear/target/globaltrade-scms.ear $WILDFLY_HOME/standalone/deployments/
```

### Test Endpoints
```bash
# Health check
curl http://localhost:8080/scms/api/timers/health

# Secure admin endpoint
curl -u admin:admin123 http://localhost:8080/scms/api/timers/secure/admin-only

# Trigger carrier failure (tests outbox resilience)
curl -i http://localhost:8080/scms/api/timers/carrier/dispatch/1002/true

# Run unit tests
mvn test
```

---

## 🧪 Testing Evidence

| Category | Tests | Status |
|----------|-------|--------|
| Timer Services | Declarative, Programmatic, Recovery | ✅ Pass |
| Interceptors | AOP Audit, Performance, Security | ✅ Pass |
| Transactions | CMT Rollback, BMT Commit/Rollback, Pessimistic Lock | ✅ Pass |
| Security | 401 Unauthorized, 403 Forbidden, RBAC, Programmatic Auth | ✅ Pass |
| Resilience | Carrier Failure 503, Outbox Persistence, Async Worker | ✅ Pass |
| Unit Tests | JUnit 5 + Mockito (InventoryServiceBean) | ✅ 2/2 Pass |

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| `docs/architecture.md` | System architecture and component interaction |
| `docs/decision-log.md` | Design decisions and alternatives evaluated |
| `docs/risk-register.md` | Identified risks and mitigation strategies |
| `Technical_Implementation_Documentation.pdf` | Full technical documentation (1,500–2,000 words) |
| `Critical_Analysis_and_Test_Report.pdf` | Testing strategy, results, and critical evaluation (1,000–1,500 words) |

---

## 📖 References

- Eclipse Foundation. (2024). *Jakarta Enterprise Beans Specification 4.0*. https://jakarta.ee/specifications/enterprise-beans/4.0/
- Eclipse Foundation. (2024). *Jakarta Persistence Specification 3.1*. https://jakarta.ee/specifications/persistence/3.1/
- Red Hat. (2025). *WildFly Documentation*. https://docs.wildfly.org/
- PostgreSQL Global Development Group. (2025). *PostgreSQL Documentation*. https://www.postgresql.org/docs/
- OWASP. (2025). *OWASP Testing Guide*. https://owasp.org/www-project-web-security-testing-guide/

---

## 📜 License

This project was developed as an academic assignment for the **Java Institute of Advanced Technology** – Business Component Development II (JIAT/BCD II).

