package it.pagopa.pn.stream.service;

import it.pagopa.pn.stream.dto.ext.datavault.ConfidentialTimelineElementDtoInt;

public interface TimelineService {

    void enrichTimelineElementWithConfidentialInformation(Object details,
                                                          ConfidentialTimelineElementDtoInt confidentialDto);
}
