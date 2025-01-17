package it.pagopa.pn.stream.middleware.externalclient.pnclient.datavault;

import it.pagopa.pn.commons.log.PnLogger;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementId;
import reactor.core.publisher.Flux;

import java.util.List;

public interface PnDataVaultClientReactive {
    String CLIENT_NAME = PnLogger.EXTERNAL_SERVICES.PN_DATA_VAULT;
    String NOTIFICATION_TIMELINES_ADDRESS = "RETRIEVE CONFIDENTIAL INFO, NOTIFICATION TIMELINES";

    Flux<ConfidentialTimelineElementDto> getNotificationTimelines(List<ConfidentialTimelineElementId> confidentialTimelineElementId);
}
