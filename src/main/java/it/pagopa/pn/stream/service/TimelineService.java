package it.pagopa.pn.stream.service;

import it.pagopa.pn.stream.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.stream.dto.timeline.details.TimelineElementDetailsInt;

public interface TimelineService {

    void enrichTimelineElementWithConfidentialInformation(TimelineElementDetailsInt details,
                                                          ConfidentialTimelineElementDtoInt confidentialDto);
}
