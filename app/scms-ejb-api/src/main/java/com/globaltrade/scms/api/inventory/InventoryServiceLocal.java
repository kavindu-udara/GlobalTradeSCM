package com.globaltrade.scms.api.inventory;

import com.globaltrade.scms.common.exception.InventoryShortageException;

public interface InventoryServiceLocal {
    void deductStock(Long inventoryLevelId, int quantity) throws InventoryShortageException;
}
