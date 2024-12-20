package it.pagopa.pn.stream.service.impl;

import it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.StreamAction;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.StreamEventType;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.StreamsPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;


@ExtendWith(SpringExtension.class)
class SchedulerServiceImplTest {

    private StreamsPool streamsPool;

    @Mock
    private Clock clock;

    
    private SchedulerServiceImpl schedulerService;
    
    @BeforeEach
    void setup() {
        streamsPool = Mockito.mock(StreamsPool.class);
        clock = Mockito.mock(Clock.class);

        schedulerService = new SchedulerServiceImpl(streamsPool);
    }


    @Test
    void testScheduleWebhookEvent() {
        StreamAction action = StreamAction.builder()
                .streamId("01")
                .eventId("02")
                .iun("nd")
                .delay(4)
                .type(StreamEventType.REGISTER_EVENT)
                .build();

        schedulerService.scheduleWebhookEvent("01", "02", 4, StreamEventType.REGISTER_EVENT);

        Mockito.verify(streamsPool, Mockito.times(1)).scheduleFutureAction(action);
    }
}