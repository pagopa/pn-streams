package it.pagopa.pn.stream.service;

import it.pagopa.pn.stream.dto.ProgressResponseElementDto;
import java.util.List;
import java.util.UUID;

import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import reactor.core.publisher.Mono;

public interface StreamEventsService {

    Mono<ProgressResponseElementDto> consumeEventStream(String xPagopaPnCxId, List<String> xPagopaPnCxGroups, String xPagopaPnApiVersion, UUID streamId, String lastEventId);

    Mono<Void> saveEvent(TimelineElementInternal timelineElementInternal);

    Mono<Void> purgeEvents(String streamId, String eventId, boolean olderThan);
}
