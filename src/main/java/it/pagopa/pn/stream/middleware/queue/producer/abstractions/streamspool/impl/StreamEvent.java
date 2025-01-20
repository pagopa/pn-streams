package it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.impl;

import it.pagopa.pn.api.dto.events.GenericEvent;
import it.pagopa.pn.api.dto.events.StandardEventHeader;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.StreamAction;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class StreamEvent implements GenericEvent<StandardEventHeader, StreamAction> {

    private StandardEventHeader header;

    private StreamAction payload;
}
