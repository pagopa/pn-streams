package it.pagopa.pn.stream.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.exceptions.PnStreamException;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.TimelineElementCategoryV26;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.TimelineElementDetailsV26;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.TimelineElementV26;

import static it.pagopa.pn.stream.exceptions.PnStreamExceptionCodes.ERROR_CODE_GENERIC;

public class TimelineElementMapper {
    private TimelineElementMapper() {
    }

    public static TimelineElementV26 internalToExternal(TimelineElementInternal internalDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TimelineElementV26.TimelineElementV26Builder builder;
        try {
            builder = TimelineElementV26.builder()
                    .category(internalDto.getCategory() != null ? TimelineElementCategoryV26.fromValue(internalDto.getCategory()) : null)
                    .elementId(internalDto.getTimelineElementId())
                    .timestamp(internalDto.getTimestamp())
                    .notificationSentAt(internalDto.getNotificationSentAt())
                    .ingestionTimestamp(internalDto.getIngestionTimestamp())
                    .eventTimestamp(internalDto.getEventTimestamp())
                    .details(objectMapper.readValue(internalDto.getDetails(), TimelineElementDetailsV26.class).nextSourceAttemptsMade(null))
                    .legalFactsIds(internalDto.getLegalFactsIds());

        } catch (JsonProcessingException e) {
            throw new PnStreamException(e.getMessage(), 500, ERROR_CODE_GENERIC);
        }


        return builder.build();
    }


}
