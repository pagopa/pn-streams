package it.pagopa.pn.stream.dto.webhook;

import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.middleware.dao.webhook.dynamo.entity.EventEntity;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class EventTimelineInternalDto {
    private EventEntity eventEntity;
    private TimelineElementInternal timelineElementInternal;

}
