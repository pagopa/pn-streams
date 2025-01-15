package it.pagopa.pn.stream.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.stream.dto.address.PhysicalAddressInt;
import it.pagopa.pn.stream.dto.legalfacts.LegalFactCategoryInt;
import it.pagopa.pn.stream.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.dto.timeline.details.SendAnalogFeedbackDetailsInt;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.TimelineElementV26;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


class TimelineElementStreamMapperTest {

    @Test
    void fromInternalToExternal() throws JsonProcessingException {
        String category = "REQUEST_ACCEPTED";
        String elementId = "elementId";
        Instant instant = Instant.now();
        LegalFactsIdInt legalFactsIdInt = getLegalFactsIdInt(LegalFactCategoryInt.DIGITAL_DELIVERY);
        TimelineElementInternal timelineElementDetailsInt = getTimelineElementInternal(category, elementId, instant, legalFactsIdInt);

        TimelineElementV26 timelineElement = TimelineElementStreamMapper.internalToExternal(timelineElementDetailsInt);
        Assertions.assertNotNull(timelineElement);
        Assertions.assertEquals(category, timelineElement.getCategory().getValue());
        Assertions.assertEquals(elementId, timelineElement.getElementId());
        Assertions.assertEquals(instant, timelineElement.getTimestamp());
        Assertions.assertEquals(legalFactsIdInt.getCategory().getValue(), timelineElement.getLegalFactsIds().get(0).getCategory().getValue());
        Assertions.assertEquals(legalFactsIdInt.getKey(), timelineElement.getLegalFactsIds().get(0).getKey());

        legalFactsIdInt = getLegalFactsIdInt(null);
        timelineElementDetailsInt = getTimelineElementInternal(category, elementId, instant, legalFactsIdInt);
        timelineElementDetailsInt.setLegalFactsIds(null);
        timelineElement = TimelineElementStreamMapper.internalToExternal(timelineElementDetailsInt);
        Assertions.assertNotNull(timelineElement);
        Assertions.assertNull(timelineElement.getLegalFactsIds());
    }

    private LegalFactsIdInt getLegalFactsIdInt(LegalFactCategoryInt categoryInt) {
        return LegalFactsIdInt.builder()
                .key("key")
                .category(categoryInt)
                .build();
    }

    private TimelineElementInternal getTimelineElementInternal(String category, String elementId, Instant instant, LegalFactsIdInt legalFactsIdInt) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SendAnalogFeedbackDetailsInt details = SendAnalogFeedbackDetailsInt.builder()
                .newAddress(
                        PhysicalAddressInt.builder()
                                .province("province")
                                .municipality("municipality")
                                .at("at")
                                .build()
                )
                .recIndex(0)
                .sentAttemptMade(0)
                .build();

        List<LegalFactsIdInt> legalFactsIds = new ArrayList<>();
        legalFactsIds.add(legalFactsIdInt);

        return TimelineElementInternal.builder()
                .category(category)
                .elementId(elementId)
                .timestamp(instant)
                .details(objectMapper.writeValueAsString(details))
                .legalFactsIds(legalFactsIds.stream().map(legalFactsIdInt1 -> {
                    try {
                        return objectMapper.writeValueAsString(legalFactsIdInt1);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList())
                .build();
    }
}