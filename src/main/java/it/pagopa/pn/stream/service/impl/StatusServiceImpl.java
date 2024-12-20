package it.pagopa.pn.stream.service.impl;

import it.pagopa.pn.stream.dto.ext.delivery.notification.NotificationInt;
import it.pagopa.pn.stream.dto.ext.delivery.notification.status.NotificationStatusHistoryElementInt;
import it.pagopa.pn.stream.dto.ext.delivery.notification.status.NotificationStatusInt;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.service.StatusService;
import it.pagopa.pn.stream.utils.StatusUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
public class StatusServiceImpl implements StatusService {
    private final StatusUtils statusUtils;

    public StatusServiceImpl(StatusUtils statusUtils) {
        this.statusUtils = statusUtils;
    }

    @Override
    public NotificationStatusUpdate computeStatusChange(TimelineElementInternal dto, Set<TimelineElementInternal> currentTimeline, NotificationInt notification) {
        log.debug("computeStatusChange Notification is present paProtocolNumber {} for iun {}", notification.getPaProtocolNumber(), dto.getIun());

        // - Calcolare lo stato corrente
        NotificationStatusInt currentState = computeLastStatusHistoryElement(notification, currentTimeline).getStatus();
        log.debug("computeStatusChange CurrentState is {} for iun {}", currentState, dto.getIun());

        currentTimeline.add(dto);

        // - Calcolare il nuovo stato
        NotificationStatusHistoryElementInt nextState = computeLastStatusHistoryElement(notification, currentTimeline);

        log.debug("computeStatusChange Next state is {} for iun {}", nextState.getStatus(), dto.getIun());

        return new NotificationStatusUpdate(currentState, nextState.getStatus());
    }


    private NotificationStatusHistoryElementInt computeLastStatusHistoryElement(NotificationInt notification, Set<TimelineElementInternal> currentTimeline) {
        int numberOfRecipient = notification.getRecipients().size();
        Instant notificationCreatedAt = notification.getSentAt();
        List<NotificationStatusHistoryElementInt> historyElementList = statusUtils.getStatusHistory(
                currentTimeline,
                numberOfRecipient,
                notificationCreatedAt);

        return historyElementList.get(historyElementList.size() - 1);
    }

}
