package it.pagopa.pn.stream.service;

import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.WebhookEventType;

public interface SchedulerService {
    void scheduleWebhookEvent(String streamId, String eventId, Integer delay, WebhookEventType actionType);
}
