package it.pagopa.pn.stream.service.impl;

import it.pagopa.pn.stream.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.stream.service.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimeLineServiceImpl implements TimelineService {

    @Override
    public void enrichTimelineElementWithConfidentialInformation(Object details,
                                                                 ConfidentialTimelineElementDtoInt confidentialDto) {
        log.info("enrichTimelineElementWithConfidentialInformation");
    }
}
