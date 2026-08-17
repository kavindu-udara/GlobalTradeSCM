package com.globaltrade.scms.core.timer;

import com.globaltrade.scms.api.alert.AlertServiceLocal;
import com.globaltrade.scms.api.timer.CustomsTimerServiceLocal; // Import the interface
import jakarta.annotation.Resource;
import jakarta.ejb.*;

@Stateless
public class CustomsDeadlineTimerBean implements CustomsTimerServiceLocal { // Implement the interface

    @Resource
    private TimerService timerService;

    @EJB
    private AlertServiceLocal alertService;

    @Override
    public void scheduleCustomsReminder(String documentId, long delayInSeconds) {
        TimerConfig config = new TimerConfig();
        config.setInfo("CustomsDoc-" + documentId);
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