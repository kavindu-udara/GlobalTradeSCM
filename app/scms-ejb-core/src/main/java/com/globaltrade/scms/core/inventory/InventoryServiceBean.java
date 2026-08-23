package com.globaltrade.scms.core.inventory;

import com.globaltrade.scms.api.inventory.InventoryServiceLocal;
import com.globaltrade.scms.common.exception.InventoryShortageException;
import com.globaltrade.scms.core.entity.InventoryLevel;
import com.globaltrade.scms.core.interceptor.SecurityInterceptor; // Import
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors; // Import
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@Stateless
@Interceptors(SecurityInterceptor.class) // APPLIED AT CLASS LEVEL FOR EJB
public class InventoryServiceBean implements InventoryServiceLocal {

    @PersistenceContext(unitName = "ScmsPU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deductStock(Long inventoryLevelId, int quantity) throws InventoryShortageException {
        InventoryLevel level = em.find(InventoryLevel.class, inventoryLevelId, LockModeType.PESSIMISTIC_WRITE);

        if (level == null) {
            throw new InventoryShortageException("Inventory record not found.");
        }

        if (level.getQuantityAvailable() < quantity) {
            throw new InventoryShortageException("Insufficient stock. Available: " + level.getQuantityAvailable() + ", Requested: " + quantity);
        }

        level.setQuantityAvailable(level.getQuantityAvailable() - quantity);
        System.out.println("[INVENTORY] Deducted " + quantity + " units. Remaining: " + level.getQuantityAvailable());
    }
}