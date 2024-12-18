package it.pagopa.pn.stream.service.impl;

import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.WebhookAction;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.WebhookEventType;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.WebhooksPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;


@ExtendWith(SpringExtension.class)
class SchedulerServiceImplTest {

    private WebhooksPool webhooksPool;

    @Mock
    private Clock clock;

    
    private SchedulerServiceImpl schedulerService;
    
    @BeforeEach
    void setup() {
        webhooksPool = Mockito.mock(WebhooksPool.class);
        clock = Mockito.mock(Clock.class);

        schedulerService = new SchedulerServiceImpl(webhooksPool);
    }


    @Test
    void testScheduleWebhookEvent() {
        WebhookAction action = WebhookAction.builder()
                .streamId("01")
                .eventId("02")
                .iun("nd")
                .delay(4)
                .type(WebhookEventType.REGISTER_EVENT)
                .build();

        schedulerService.scheduleWebhookEvent("01", "02", 4, WebhookEventType.REGISTER_EVENT);

        Mockito.verify(webhooksPool, Mockito.times(1)).scheduleFutureAction(action);
    }
}