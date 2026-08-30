package com.globaltrade.scms.core.carrier;

import com.globaltrade.scms.api.carrier.CarrierServiceLocal;
import com.globaltrade.scms.api.outbox.OutboxServiceLocal;
import com.globaltrade.scms.common.exception.CarrierSystemException;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class CarrierIntegrationBean implements CarrierServiceLocal {

    @EJB
    private OutboxServiceLocal outboxService;

    @Override
    public void dispatchToCarrier(Long shipmentId, boolean simulateOutage) {

        System.out.println("[CARRIER] Attempting to dispatch shipment " + shipmentId + " to external API...");

        try {

            if (simulateOutage) {
                throw new RuntimeException("Connection timed out: External Carrier Gateway is DOWN!");
            }

            System.out.println("[CARRIER] Successfully dispatched shipment " + shipmentId + " to external API.");

        } catch (Exception e) {

            System.err.println("[CARRIER] External API failed. Saving request to Outbox.");

            String payload = "{\"shipmentId\":" + shipmentId + ", \"action\":\"DISPATCH\"}";

            // Save in a separate REQUIRES_NEW transaction so it survives rollback
            outboxService.saveFailedIntegration(
                    shipmentId,
                    "SHIPMENT",
                    payload,
                    e.getMessage()
            );

            throw new CarrierSystemException(
                    "Carrier system unavailable. Request queued for automatic retry.",
                    e
            );
        }
    }
}