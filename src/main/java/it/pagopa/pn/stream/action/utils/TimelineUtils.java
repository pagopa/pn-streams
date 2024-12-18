package it.pagopa.pn.stream.action.utils;

import it.pagopa.pn.stream.dto.timeline.*;
import it.pagopa.pn.stream.service.TimelineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;
import static it.pagopa.pn.stream.dto.timeline.TimelineEventId.*;

@Component
@Slf4j
public class TimelineUtils {

    private final TimelineService timelineService;

    public TimelineUtils(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    public boolean checkIsNotificationCancellationRequested(String iun) {
        String elementId = NOTIFICATION_CANCELLATION_REQUEST.buildEventId(
                EventId.builder()
                        .iun(iun)
                        .build());

        Set<TimelineElementInternal> notificationElements = timelineService.getTimelineByIunTimelineId(iun, elementId, false);

        boolean isNotificationCancelled = notificationElements != null && !notificationElements.isEmpty();
        log.debug("NotificationCancelled value is={}", isNotificationCancelled);

        return isNotificationCancelled;
    }
}
