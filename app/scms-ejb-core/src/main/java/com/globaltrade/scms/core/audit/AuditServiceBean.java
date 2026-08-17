package com.globaltrade.scms.core.audit;

import com.globaltrade.scms.core.entity.AuditLog;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class AuditServiceBean {

    @PersistenceContext(unitName = "ScmsPU")
    private EntityManager em;

    // REQUIRES_NEW ensures the audit log is saved even if the main business transaction fails and rolls back
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveAuditLog(String username, String action, String module, String outcome) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setModuleName(module);
        log.setOutcome(outcome);
        em.persist(log);
    }
}
