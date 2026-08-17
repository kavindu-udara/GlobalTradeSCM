package com.globaltrade.scms.core.inventory;

import com.globaltrade.scms.api.inventory.InventoryServiceLocal;
import com.globaltrade.scms.common.exception.InventoryShortageException;
import com.globaltrade.scms.core.entity.InventoryLevel;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@Stateless
public class InventoryServiceBean implements InventoryServiceLocal {

    @PersistenceContext(unitName = "ScmsPU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deductStock(Long inventoryLevelId, int quantity) throws InventoryShortageException {
        // PESSIMISTIC_WRITE locks the row in the database until this transaction commits/rolls back
        InventoryLevel level = em.find(InventoryLevel.class, inventoryLevelId, LockModeType.PESSIMISTIC_WRITE);

        if (level == null) {
            throw new InventoryShortageException("Inventory record not found.");
        }

        if (level.getQuantityAvailable() < quantity) {
            // Throwing this ApplicationException(rollback=true) will automatically abort the transaction!
            throw new InventoryShortageException("Insufficient stock. Available: " + level.getQuantityAvailable() + ", Requested: " + quantity);
        }

        level.setQuantityAvailable(level.getQuantityAvailable() - quantity);
        System.out.println("[INVENTORY] Deducted " + quantity + " units. Remaining: " + level.getQuantityAvailable());
    }
}