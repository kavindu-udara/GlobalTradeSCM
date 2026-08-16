package com.globaltrade.scms.api.alert;

public interface AlertServiceLocal {
    void createAlert(String type, String severity, String message);
    long countUnacknowledgedAlerts();
}
