package it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.impl;

import it.pagopa.pn.api.dto.events.StandardEventHeader;
import it.pagopa.pn.api.dto.events.MomProducer;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.StreamAction;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.StreamsPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.UUID;

@Service
@Slf4j
public class StreamsPoolImpl implements StreamsPool {

    private final MomProducer<StreamEvent> actionsQueue;

    private final Clock clock;


    public StreamsPoolImpl(MomProducer<StreamEvent> actionsQueue,
                           Clock clock ) {
        this.actionsQueue = actionsQueue;
        this.clock = clock;
    }


    @Override
    public void scheduleFutureAction(StreamAction action) {
        // prevedere la gestione del delay passato nella action in fase di inserimento
        addWebhookAction(action);
    }

    private void addWebhookAction(StreamAction action ) {
        actionsQueue.push( StreamEvent.builder()
                .header( StandardEventHeader.builder()
                        .publisher("stream")
                        .iun( action.getIun() )
                        .eventId(UUID.randomUUID().toString())
                        .createdAt( clock.instant() )
                        .eventType( StreamActionEventType.WEBHOOK_ACTION_GENERIC.name())
                        .build()
                )
                .payload( action )
                .build()
        );
    }
}
