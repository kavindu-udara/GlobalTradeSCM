package com.globaltrade.scms.api.carrier;

public interface CarrierServiceLocal {
    void dispatchToCarrier(Long shipmentId, boolean simulateOutage);
}