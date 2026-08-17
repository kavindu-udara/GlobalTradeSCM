package com.globaltrade.scms.core.shipment;

import com.globaltrade.scms.api.shipment.ShipmentServiceLocal;
import com.globaltrade.scms.core.interceptor.PerformanceInterceptor; // Import
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors; // Import
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless(name = "ShipmentServiceBean")
public class ShipmentServiceBean implements ShipmentServiceLocal {

    @PersistenceContext(unitName = "ScmsPU")
    private EntityManager em;

    @Override
    public long countShipments() { return 0; }

    @Override
    public long countVendors() {
        return em.createQuery("SELECT COUNT(v) FROM Vendor v", Long.class).getSingleResult();
    }

    @Override
    public String getServiceStatus() { return "Shipment service is running"; }

    // METHOD-LEVEL INTERCEPTOR: Only applied to this specific heavy method
    @Interceptors(PerformanceInterceptor.class)
    public void simulateHeavyRouteOptimization() {
        System.out.println("[SHIPMENT SERVICE] Running complex route optimization algorithm...");
        try {
            Thread.sleep(1500); // Simulate 1.5 seconds of heavy processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[SHIPMENT SERVICE] Route optimization complete.");
    }
}