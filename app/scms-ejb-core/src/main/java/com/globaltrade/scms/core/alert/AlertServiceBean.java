package com.globaltrade.scms.core.alert;

import com.globaltrade.scms.api.alert.AlertServiceLocal;
import com.globaltrade.scms.core.entity.Alert;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class AlertServiceBean implements AlertServiceLocal {

    @PersistenceContext(unitName = "ScmsPU")
    private EntityManager em;

    @Override
    public void createAlert(String type, String severity, String message) {
        Alert alert = new Alert();
        alert.setAlertType(type);
        alert.setSeverity(severity);
        alert.setMessage(message);
        em.persist(alert);
        System.out.println("[ALERT CREATED] " + severity + ": " + message);
    }

    @Override
    public long countUnacknowledgedAlerts() {
        return em.createQuery("SELECT COUNT(a) FROM Alert a WHERE a.acknowledged = false", Long.class).getSingleResult();
    }
}