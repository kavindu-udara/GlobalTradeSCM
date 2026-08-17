package com.globaltrade.scms.api.timer;

public interface CustomsTimerServiceLocal {
    void scheduleCustomsReminder(String documentId, long delayInSeconds);
}