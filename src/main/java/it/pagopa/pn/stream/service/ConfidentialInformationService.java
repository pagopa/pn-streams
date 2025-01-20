package it.pagopa.pn.stream.service;

import it.pagopa.pn.stream.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ConfidentialInformationService {
        Flux<ConfidentialTimelineElementDtoInt> getTimelineConfidentialInformation(List<TimelineElementInternal> timelineElementInternal);


}
