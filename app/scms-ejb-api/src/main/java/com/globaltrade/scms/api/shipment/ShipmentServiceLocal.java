package com.globaltrade.scms.api.shipment;

public interface ShipmentServiceLocal {
    long countShipments();
    long countVendors(); // NEW METHOD
    String getServiceStatus();
}