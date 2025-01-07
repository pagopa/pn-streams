package it.pagopa.pn.stream.service;

import it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.StreamEventType;

public interface SchedulerService {
    void scheduleWebhookEvent(String streamId, String eventId, Integer delay, StreamEventType actionType);
}
