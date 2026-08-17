package com.globaltrade.scms.common.exception;

import jakarta.ejb.ApplicationException;

// Forces the EJB container to rollback the transaction when this exception is thrown
@ApplicationException(rollback = true)
public class InventoryShortageException extends Exception {

    public InventoryShortageException(String message) {
        super(message);
    }
}
