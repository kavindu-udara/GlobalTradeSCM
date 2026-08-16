package com.globaltrade.scms.core.timer;

import com.globaltrade.scms.api.alert.AlertServiceLocal;
import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Singleton
@Startup
public class InventoryMonitorTimerBean {

    @PersistenceContext(unitName = "ScmsPU")
    private EntityManager em;

    @EJB
    private AlertServiceLocal alertService;

    // Runs every 30 seconds for testing. In production: hour="*", minute="0"
    // persistent=false is used here because if the server restarts, we don't need to catch up on missed 30-second ticks.
    @Schedule(hour="*", minute="*", second="*/30", persistent=false, info="InventoryMonitor")
    public void checkInventoryLevels(Timer timer) {
        System.out.println("[TIMER - Declarative] Running inventory check...");
        try {
            long lowStockCount = em.createQuery(
                            "SELECT COUNT(i) FROM InventoryLevel i WHERE i.quantityAvailable <= i.reorderLevel", Long.class)
                    .getSingleResult();

            if (lowStockCount > 0) {
                alertService.createAlert("INVENTORY_SHORTAGE", "WARNING", "Found " + lowStockCount + " items below reorder level.");
            }
        } catch (Exception e) {
            System.out.println("[TIMER] Error checking inventory: " + e.getMessage());
        }
    }
}
