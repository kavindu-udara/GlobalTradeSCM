package com.globaltrade.scms.core.timer;

import com.globaltrade.scms.api.alert.AlertServiceLocal;
import jakarta.annotation.Resource;
import jakarta.ejb.*;

@Stateless
public class CustomsDeadlineTimerBean {

    @Resource
    private TimerService timerService;

    @EJB
    private AlertServiceLocal alertService;

    public void scheduleCustomsReminder(String documentId, long delayInSeconds) {
        TimerConfig config = new TimerConfig();
        config.setInfo("CustomsDoc-" + documentId);

        // persistent=true is CRITICAL for the assignment.
        // If the server crashes, the timer survives and fires when the server restarts.
        config.setPersistent(true);

        timerService.createSingleActionTimer(delayInSeconds * 1000, config);
        System.out.println("[TIMER - Programmatic] Scheduled customs reminder for doc " + documentId + " in " + delayInSeconds + " seconds.");
    }

    @Timeout
    public void handleCustomsDeadline(Timer timer) {
        String docInfo = (String) timer.getInfo();
        System.out.println("[TIMER - Programmatic] Customs deadline reached for: " + docInfo);
        alertService.createAlert("CUSTOMS_DEADLINE", "CRITICAL", "Customs deadline reached for " + docInfo);
    }
}