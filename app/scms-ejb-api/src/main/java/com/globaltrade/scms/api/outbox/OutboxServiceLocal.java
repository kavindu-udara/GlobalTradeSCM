package com.globaltrade.scms.api.outbox;

public interface OutboxServiceLocal {
    void saveFailedIntegration(Long aggregateId,
                               String aggregateType,
                               String payload,
                               String errorMessage);
}