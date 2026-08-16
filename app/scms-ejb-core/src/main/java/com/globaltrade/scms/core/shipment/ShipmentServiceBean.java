package com.globaltrade.scms.core.shipment;

import com.globaltrade.scms.api.shipment.ShipmentServiceLocal;

import jakarta.ejb.Stateless;

@Stateless(name = "ShipmentServiceBean")
public class ShipmentServiceBean implements ShipmentServiceLocal {

    @Override
    public long countShipments() {
        return 0;
    }

    @Override
    public String getServiceStatus() {
        return "Shipment service is running";
    }

}