package it.pagopa.pn.stream.action.it.mockbean;

import it.pagopa.pn.stream.dto.stream.ProgressResponseElementDto;
import it.pagopa.pn.stream.service.StreamEventsService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public class StreamEventsServiceMock implements StreamEventsService {
    @Override
    public Mono<ProgressResponseElementDto> consumeEventStream(String xPagopaPnCxId, List<String> xPagopaPnCxGroups, String xPagopaPnApiVersion, UUID streamId, String lastEventId) {
        return null;
    }
}
