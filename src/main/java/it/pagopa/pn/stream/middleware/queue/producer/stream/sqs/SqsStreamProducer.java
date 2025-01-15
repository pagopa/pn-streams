package it.pagopa.pn.stream.middleware.queue.producer.stream.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.api.dto.events.AbstractSqsMomProducer;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.impl.StreamEvent;
import software.amazon.awssdk.services.sqs.SqsClient;

public class SqsStreamProducer extends AbstractSqsMomProducer<StreamEvent> {

    public SqsStreamProducer(SqsClient sqsClient, String topic, ObjectMapper objectMapper ) {
        super(sqsClient, topic, objectMapper, StreamEvent.class );
    }
}
