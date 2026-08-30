package com.globaltrade.scms.core.notification;

import com.globaltrade.scms.api.notification.NotificationServiceLocal;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;

@Stateless
public class NotificationServiceBean implements NotificationServiceLocal {

    @Override
    @Asynchronous
    public void sendVendorReplenishmentAlert(Long inventoryLevelId, String message) {
        System.out.println("=======================================================");
        System.out.println("[ASYNC WORKER] Starting background email process for Inventory ID: " + inventoryLevelId);

        try {
            // Simulate network latency to an external SMTP/Email server (3 sec)
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("[ASYNC WORKER] Email sent to Vendor! Message: " + message);
        System.out.println("=======================================================");
    }
}