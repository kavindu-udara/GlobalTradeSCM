package com.globaltrade.scms.core.shipment;

import com.globaltrade.scms.api.shipment.ShipmentServiceLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless(name = "ShipmentServiceBean")
public class ShipmentServiceBean implements ShipmentServiceLocal {

    @PersistenceContext(unitName = "ScmsPU")
    private EntityManager em;

    @Override
    public long countShipments() {
        return 0;
    }

    @Override
    public long countVendors() {
        // JPQL query to count vendors in the database
        return em.createQuery("SELECT COUNT(v) FROM Vendor v", Long.class).getSingleResult();
    }

    @Override
    public String getServiceStatus() {
        return "Shipment service is running";
    }
}