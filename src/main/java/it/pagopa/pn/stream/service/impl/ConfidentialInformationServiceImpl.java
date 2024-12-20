package it.pagopa.pn.stream.service.impl;

import it.pagopa.pn.stream.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.middleware.externalclient.pnclient.datavault.PnDataVaultClient;
import it.pagopa.pn.stream.middleware.externalclient.pnclient.datavault.PnDataVaultClientReactive;
import it.pagopa.pn.stream.service.ConfidentialInformationService;
import it.pagopa.pn.stream.service.mapper.ConfidentialTimelineElementDtoMapper;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConfidentialInformationServiceImpl implements ConfidentialInformationService {
    private final PnDataVaultClient pnDataVaultClient;
    private final PnDataVaultClientReactive pnDataVaultClientReactive;

    public ConfidentialInformationServiceImpl(PnDataVaultClient pnDataVaultClient,
                                              PnDataVaultClientReactive pnDataVaultClientReactive) {
        this.pnDataVaultClient = pnDataVaultClient;
        this.pnDataVaultClientReactive = pnDataVaultClientReactive;
    }

    @Override
    public Flux<ConfidentialTimelineElementDtoInt> getTimelineConfidentialInformation(List<TimelineElementInternal> timelineElementInternal) {
        List<ConfidentialTimelineElementId> request = timelineElementInternal.stream().map(this::getConfidentialElementId).toList();
        return this.pnDataVaultClientReactive.getNotificationTimelines(request)
                .map( ConfidentialTimelineElementDtoMapper::externalToInternal);
    }

    private ConfidentialTimelineElementId getConfidentialElementId(TimelineElementInternal internal) {
        return ConfidentialTimelineElementId.builder()
                .iun(internal.getIun())
                .timelineElementId(internal.getElementId())
                .build();
    }

    @Override
    public Optional<Map<String, ConfidentialTimelineElementDtoInt>> getTimelineConfidentialInformation(String iun) {
        List<ConfidentialTimelineElementDto> listDtoExt = pnDataVaultClient.getNotificationTimelineByIunWithHttpInfo(iun);

        log.debug("getTimelineConfidentialInformation OK for - iun {} ", iun);

        if (listDtoExt != null && !listDtoExt.isEmpty()) {
            return Optional.of(
                    listDtoExt.stream()
                            .map(ConfidentialTimelineElementDtoMapper::externalToInternal)
                            .collect(Collectors.toMap(ConfidentialTimelineElementDtoInt::getTimelineElementId, Function.identity()))
            );
        }
        log.debug("getTimelineConfidentialInformation haven't confidential information for - iun {} ", iun);
        return Optional.empty();

    }
}
