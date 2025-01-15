package it.pagopa.pn.stream.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.LegalFactsIdV20;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.TimelineElementCategoryV26;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.TimelineElementDetailsV26;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.TimelineElementV26;

public class TimelineElementMapper {
    private TimelineElementMapper(){}
    
    public static TimelineElementV26 internalToExternal(TimelineElementInternal internalDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TimelineElementV26.TimelineElementV26Builder builder = null;
        try {
            builder = TimelineElementV26.builder()
                    .category(internalDto.getCategory() != null ? TimelineElementCategoryV26.fromValue( internalDto.getCategory() ) : null)
                    .elementId(internalDto.getElementId())
                    .timestamp(internalDto.getTimestamp())
                    .notificationSentAt(internalDto.getNotificationSentAt())
                    .ingestionTimestamp(internalDto.getIngestionTimestamp())
                    .eventTimestamp(internalDto.getEventTimestamp())
                    .details(objectMapper.readValue(internalDto.getDetails(), TimelineElementDetailsV26.class));

            if(internalDto.getLegalFactsIds() != null){
                builder.legalFactsIds(
                        internalDto.getLegalFactsIds().stream()
                                .map(s -> {
                                    try {
                                        return objectMapper.readValue(s, LegalFactsIdV20.class);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                                .toList()
                );
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }



        return builder.build();
    }


}
