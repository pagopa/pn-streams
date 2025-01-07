package it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public class StreamAction {

    private String streamId;

    private String eventId;

    private String paId;

    private String iun;

    private Integer delay;

    private String timelineId;

    private StreamEventType type;
}
