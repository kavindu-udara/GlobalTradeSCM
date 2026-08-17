package com.globaltrade.scms.api.customs;

public interface CustomsClearanceServiceLocal {
    String processCustomsClearance(Long shipmentId, boolean simulateExternalFailure);
}