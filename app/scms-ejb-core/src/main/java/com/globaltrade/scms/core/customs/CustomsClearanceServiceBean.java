package com.globaltrade.scms.core.customs;

import com.globaltrade.scms.api.customs.CustomsClearanceServiceLocal;
import com.globaltrade.scms.api.interceptor.LogisticsAudit;
import com.globaltrade.scms.core.entity.Alert;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;

@Stateless
// Switch from Container-Managed to Bean-Managed Transactions
@TransactionManagement(TransactionManagementType.BEAN)
public class CustomsClearanceServiceBean implements CustomsClearanceServiceLocal {

    @PersistenceContext(unitName = "ScmsPU")
    private EntityManager em;

    @Resource
    private UserTransaction utx; // Inject the manual transaction manager

    @Override
    @LogisticsAudit(module = "CUSTOMS_GATEWAY", requiresComplianceCheck = true)
    public String processCustomsClearance(Long shipmentId, boolean simulateExternalFailure) {
        try {
            utx.begin(); // Manually start transaction
            System.out.println("[CUSTOMS BMT] Transaction started for shipment " + shipmentId);

            // Step 1: Save a preliminary audit alert
            Alert alert = new Alert();
            alert.setAlertType("CUSTOMS_PROCESSING");
            alert.setSeverity("INFO");
            alert.setMessage("Initiating customs clearance for shipment " + shipmentId);
            alert.setAcknowledged(false);
            em.persist(alert);

            // Step 2: Simulate calling an external Global Customs Gateway API
            System.out.println("[CUSTOMS BMT] Calling external Customs Gateway...");
            if (simulateExternalFailure) {
                throw new Exception("External Customs Gateway Timeout!");
            }

            utx.commit(); // Manually commit if everything succeeds
            return "Customs clearance successful for shipment " + shipmentId;

        } catch (Exception e) {
            try {
                System.err.println("[CUSTOMS BMT] Failure detected: " + e.getMessage() + ". Rolling back transaction!");
                utx.rollback(); // Manually rollback the Alert insertion
                return "Customs clearance FAILED and rolled back: " + e.getMessage();
            } catch (Exception rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction", rollbackEx);
            }
        }
    }
}