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
}