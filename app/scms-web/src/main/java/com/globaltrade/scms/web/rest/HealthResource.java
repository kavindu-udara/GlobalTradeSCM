package com.globaltrade.scms.web.rest;

import com.globaltrade.scms.api.shipment.ShipmentServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/health")
public class HealthResource {

    @EJB
    private ShipmentServiceLocal shipmentService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        long vendorCount = shipmentService.countVendors();
        return "OK - " + shipmentService.getServiceStatus() + " | Active Vendors in DB: " + vendorCount;
    }
}