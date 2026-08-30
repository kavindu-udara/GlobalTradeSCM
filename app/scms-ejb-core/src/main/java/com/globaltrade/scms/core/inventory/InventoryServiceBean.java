package com.globaltrade.scms.core.inventory;

import com.globaltrade.scms.api.inventory.InventoryServiceLocal;
import com.globaltrade.scms.api.notification.NotificationServiceLocal;
import com.globaltrade.scms.common.exception.InventoryShortageException;
import com.globaltrade.scms.core.entity.InventoryLevel;
import com.globaltrade.scms.core.interceptor.SecurityInterceptor;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.jms.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@Stateless
@Interceptors(SecurityInterceptor.class)
public class InventoryServiceBean implements InventoryServiceLocal {

    @PersistenceContext(unitName = "ScmsPU")
    private EntityManager em;

    @EJB
    private NotificationServiceLocal notificationService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deductStock(Long inventoryLevelId, int quantity) throws InventoryShortageException {
        InventoryLevel level = em.find(InventoryLevel.class, inventoryLevelId, LockModeType.PESSIMISTIC_WRITE);

        if (level == null) throw new InventoryShortageException("Inventory record not found.");
        if (level.getQuantityAvailable() < quantity) {
            throw new InventoryShortageException("Insufficient stock. Available: " + level.getQuantityAvailable());
        }

        level.setQuantityAvailable(level.getQuantityAvailable() - quantity);

        // If stock drops below reorder level, fire an async event
        if (level.getQuantityAvailable() <= level.getReorderLevel()) {
            // This returns INSTANTLY
            notificationService.sendVendorReplenishmentAlert(inventoryLevelId, "Stock dropped below reorder level!");
        }
    }

}