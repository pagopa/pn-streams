package it.pagopa.pn.stream.service.mapper;

import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.generated.openapi.server.webhook.v1.dto.TimelineElementV25;


public class TimelineElementWebhookMapper {
    private TimelineElementWebhookMapper(){}

    public static TimelineElementV25 internalToExternal(TimelineElementInternal internalDto) {
        // passo da TimelineElementMapper.internalToExternal(internalDto) in modo da replicare gli stessi controlli già presenti per il mapper di delivery push
        TimelineElementV25 timelineElement = TimelineElementMapper.internalToExternal(internalDto);
        return SmartMapper.mapToClass(timelineElement, TimelineElementV25.class);
    }
}