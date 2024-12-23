package it.pagopa.pn.stream.service.mapper;

import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.generated.openapi.server.webhook.v1.dto.TimelineElementCategoryV23;
import it.pagopa.pn.stream.generated.openapi.server.webhook.v1.dto.TimelineElementDetailsV25;
import it.pagopa.pn.stream.generated.openapi.server.webhook.v1.dto.TimelineElementV25;

public class TimelineElementMapper {
    private TimelineElementMapper(){}
    
    public static TimelineElementV25 internalToExternal(TimelineElementInternal internalDto) {
        var builder = TimelineElementV25.builder()
                .category(internalDto.getCategory() != null ? TimelineElementCategoryV23.fromValue( internalDto.getCategory().getValue() ) : null)
                .elementId(internalDto.getElementId())
                .timestamp(internalDto.getTimestamp())
                .notificationSentAt(internalDto.getNotificationSentAt())
                .ingestionTimestamp(internalDto.getIngestionTimestamp())
                .eventTimestamp(internalDto.getEventTimestamp())
                .details( SmartMapper.mapToClass(internalDto.getDetails(), TimelineElementDetailsV25.class) );
        
        if(internalDto.getLegalFactsIds() != null){
            builder.legalFactsIds(
                    internalDto.getLegalFactsIds().stream()
                            .map(LegalFactIdMapper::internalToExternal)
                            .toList()
            );
        }

        return builder.build();
    }


}