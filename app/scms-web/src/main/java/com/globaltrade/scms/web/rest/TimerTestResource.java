package com.globaltrade.scms.web.rest;

import com.globaltrade.scms.api.alert.AlertServiceLocal;
import com.globaltrade.scms.api.timer.CustomsTimerServiceLocal; // Import the API interface
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/timers")
public class TimerTestResource {

    // Inject the Interface, NOT the implementation class
    @EJB
    private CustomsTimerServiceLocal customsTimer;

    @EJB
    private AlertServiceLocal alertService;

    @EJB
    private com.globaltrade.scms.api.shipment.ShipmentServiceLocal shipmentService;

    @EJB
    private com.globaltrade.scms.api.inventory.InventoryServiceLocal inventoryService;

    @EJB
    private com.globaltrade.scms.api.customs.CustomsClearanceServiceLocal customsService;

    @GET
    @Path("/schedule-customs/{docId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String schedule(@PathParam("docId") String docId) {
        // Schedule a timer to fire in 15 seconds
        customsTimer.scheduleCustomsReminder(docId, 15);
        return "Scheduled programmatic timer for " + docId + " to fire in 15 seconds. Check WildFly console!";
    }

    @GET
    @Path("/alerts-count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAlerts() {
        return "Unacknowledged Alerts in DB: " + alertService.countUnacknowledgedAlerts();
    }

    @GET
    @Path("/optimize-route")
    @Produces(MediaType.TEXT_PLAIN)
    public String optimizeRoute() {
        shipmentService.simulateHeavyRouteOptimization();
        return "Route optimization executed. Check console for Performance Interceptor output!";
    }

    // Endpoint 1: Test CMT + Pessimistic Locking + ApplicationException Rollback
    @GET
    @Path("/inventory/deduct/{id}/{qty}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deductInventory(@PathParam("id") Long id, @PathParam("qty") int qty) {
        try {
            inventoryService.deductStock(id, qty);
            return "Stock deducted successfully.";
        } catch (com.globaltrade.scms.common.exception.InventoryShortageException e) {
            // Because of @ApplicationException(rollback=true), the transaction was already rolled back by the container!
            return "Transaction Rolled Back! Reason: " + e.getMessage();
        }
    }

    // Endpoint 2: Test BMT (Manual Commit/Rollback)
    @GET
    @Path("/customs/clear/{shipmentId}/{simulateFailure}")
    @Produces(MediaType.TEXT_PLAIN)
    public String clearCustoms(@PathParam("shipmentId") Long shipmentId, @PathParam("simulateFailure") boolean simulateFailure) {
        return customsService.processCustomsClearance(shipmentId, simulateFailure);
    }
}