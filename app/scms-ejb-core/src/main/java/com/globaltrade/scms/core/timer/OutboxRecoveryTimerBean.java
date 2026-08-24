package com.globaltrade.scms.core.timer;

import com.globaltrade.scms.core.entity.IntegrationOutbox;
import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Singleton
@Startup
public class OutboxRecoveryTimerBean {

    @PersistenceContext(unitName = "ScmsPU")
    private EntityManager em;

    // Runs every 1 minute to check for failed external integrations
    @Schedule(hour="*", minute="*", second="0", persistent=false, info="OutboxRecovery")
    public void retryFailedIntegrations(Timer timer) {
        List<IntegrationOutbox> pendingMessages = em.createQuery(
                        "SELECT o FROM IntegrationOutbox o WHERE o.status = 'PENDING' OR o.status = 'FAILED'", IntegrationOutbox.class)
                .setMaxResults(10) // Process in batches to prevent memory overload
                .getResultList();

        if (!pendingMessages.isEmpty()) {
            System.out.println("[RECOVERY TIMER] Found " + pendingMessages.size() + " pending outbox messages. Retrying...");
        }

        for (IntegrationOutbox msg : pendingMessages) {
            try {
                // Simulate retrying the external API call
                System.out.println("[RECOVERY TIMER] Retrying dispatch for Shipment ID: " + msg.getAggregateId());

                // Simulate success on retry
                msg.setStatus("COMPLETED");
                msg.setProcessedAt(LocalDateTime.now());
                em.merge(msg);

            } catch (Exception e) {
                // If it fails again, increment retry count
                msg.setRetryCount(msg.getRetryCount() + 1);
                msg.setStatus("FAILED");
                msg.setErrorMessage("Retry failed: " + e.getMessage());
                em.merge(msg);
            }
        }
    }
}