package it.pagopa.pn.stream.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.stream.dto.address.PhysicalAddressInt;
import it.pagopa.pn.stream.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class TimeLineServiceImplTest {
    private TimeLineServiceImpl timeLineService;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        timeLineService = new TimeLineServiceImpl( mapper);
    }

    @Test
    void getTimeline() {

        String timelineId2 = "idTimeline2";
        String timelineId3 = "idTimeline3";

        Map<String, ConfidentialTimelineElementDtoInt> mapConfInf = new HashMap<>();
        ConfidentialTimelineElementDtoInt confInfDigital = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId(timelineId2)
                .digitalAddress("prova@prova.com")
                .build();
        ConfidentialTimelineElementDtoInt confInfPhysical = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId(timelineId3)
                .physicalAddress(
                        PhysicalAddressInt.builder()
                                .at("at")
                                .municipality("muni")
                                .province("NA")
                                .addressDetails("details")
                                .build()
                )
                .build();
        mapConfInf.put(confInfDigital.getTimelineElementId(), confInfDigital);
        mapConfInf.put(confInfPhysical.getTimelineElementId(), confInfPhysical);

        String physical = timeLineService.enrichTimelineElementWithConfidentialInformation(null, "{\"recIndex\":0,\"physicalAddress\":{\"zip\":\"87100\",\"foreignState\":\"ITALIA\"},\"nextSourceAttemptsMade\":0}", confInfPhysical);
        String digital = timeLineService.enrichTimelineElementWithConfidentialInformation(null, "{\"recIndex\":0,\"digitalAddress\":{\"type\":\"SERCQ\"}}", confInfDigital);

        Assertions.assertTrue(digital.contains("\"address\":\"prova@prova.com\""));
        Assertions.assertTrue(physical.contains("\"at\":\"at\""));
        Assertions.assertTrue(physical.contains("\"municipality\":\"muni\""));
        Assertions.assertTrue(physical.contains("\"province\":\"NA\""));
        Assertions.assertTrue(physical.contains("\"addressDetails\":\"details\""));
    }

}
