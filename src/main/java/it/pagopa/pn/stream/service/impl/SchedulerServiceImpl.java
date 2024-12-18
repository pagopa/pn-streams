package it.pagopa.pn.stream.service.impl;

import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.WebhookAction;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.WebhookEventType;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.WebhooksPool;
import it.pagopa.pn.stream.service.SchedulerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {
    private final WebhooksPool webhooksPool;
    
    @Override
    public void scheduleWebhookEvent(String streamId, String eventId, Integer delay, WebhookEventType actionType) {
        WebhookAction action = WebhookAction.builder()
                .streamId(streamId)
                .eventId(eventId)
                .iun("nd")
                .delay(delay)
                .type(actionType)
                .build();

        this.webhooksPool.scheduleFutureAction(action);
    }
}
