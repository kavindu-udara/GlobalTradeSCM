package com.globaltrade.scms.core.outbox;

import com.globaltrade.scms.api.outbox.OutboxServiceLocal;
import com.globaltrade.scms.core.entity.IntegrationOutbox;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class OutboxServiceBean implements OutboxServiceLocal {

    @PersistenceContext(unitName = "ScmsPU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveFailedIntegration(Long aggregateId,
                                      String aggregateType,
                                      String payload,
                                      String errorMessage) {

        IntegrationOutbox outbox = new IntegrationOutbox();

        outbox.setAggregateType(aggregateType);
        outbox.setAggregateId(aggregateId);
        outbox.setPayload(payload);
        outbox.setStatus("PENDING");
        outbox.setRetryCount(0);
        outbox.setErrorMessage(errorMessage);

        em.persist(outbox);

        System.out.println("[OUTBOX] Failed integration saved for aggregate ID: " + aggregateId);
    }
}