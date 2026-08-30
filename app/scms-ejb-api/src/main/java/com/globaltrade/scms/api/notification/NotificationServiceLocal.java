package com.globaltrade.scms.api.notification;

public interface NotificationServiceLocal {
    void sendVendorReplenishmentAlert(Long inventoryLevelId, String message);
}