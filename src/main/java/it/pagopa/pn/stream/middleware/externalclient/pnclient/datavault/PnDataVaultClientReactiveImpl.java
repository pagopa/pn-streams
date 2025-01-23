package it.pagopa.pn.stream.middleware.externalclient.pnclient.datavault;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.pnclients.CommonBaseClient;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.BaseRecipientDto;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementId;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.NotificationRecipientAddressesDto;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault_reactive.api.NotificationsApi;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault_reactive.api.RecipientsApi;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static it.pagopa.pn.stream.exceptions.PnStreamExceptionCodes.*;

@Component
@RequiredArgsConstructor
@CustomLog
public class PnDataVaultClientReactiveImpl extends CommonBaseClient implements PnDataVaultClientReactive {
    private final RecipientsApi recipientsApi;
    private final NotificationsApi notificationApi;

    @Override
    @Retryable(
            value = {PnInternalException.class},
            maxAttempts = 3,
            backoff = @Backoff(random = true, delay = 500, maxDelay = 1000, multiplier = 2)
    )
    public Flux<ConfidentialTimelineElementDto> getNotificationTimelines(List<ConfidentialTimelineElementId> confidentialTimelineElementId) {
        log.logInvokingExternalService(CLIENT_NAME, NOTIFICATION_TIMELINES_ADDRESS);
        return notificationApi.getNotificationTimelines(confidentialTimelineElementId)
                .onErrorResume( err -> {
                    log.error("Exception invoking getNotificationTimelines with confidentialTimelineElementId list={} err ", confidentialTimelineElementId, err);
                    return Mono.error(new PnInternalException("Exception invoking getNotificationTimelines ", ERROR_CODE_DATAVAULT_FAILED, err));
                });
    }
}
