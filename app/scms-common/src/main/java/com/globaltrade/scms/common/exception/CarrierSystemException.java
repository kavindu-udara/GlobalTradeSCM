package com.globaltrade.scms.common.exception;

// System exceptions in EJB are typically RuntimeExceptions.
// They automatically trigger transaction rollbacks and indicate infrastructure failures.
public class CarrierSystemException extends RuntimeException {

    public CarrierSystemException(String message) {
        super(message);
    }

    public CarrierSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}