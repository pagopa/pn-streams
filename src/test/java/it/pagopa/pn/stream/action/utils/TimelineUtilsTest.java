package it.pagopa.pn.stream.action.utils;

import it.pagopa.pn.stream.dto.timeline.EventId;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.dto.timeline.TimelineEventId;
import it.pagopa.pn.stream.dto.timeline.details.*;
import it.pagopa.pn.stream.service.TimelineService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
class TimelineUtilsTest {

    @Mock
    private TimelineService timelineService;

    private TimelineUtils timelineUtils;

    @BeforeEach
    void setUp() {
        timelineService = mock(TimelineService.class);
        timelineUtils = new TimelineUtils(timelineService);
    }

    @Test
    void checkIsNotificationCancellationNotRequested() {
        String iun = "IUN-checkIsNotificationCancellationNotRequested";

        Mockito.when(timelineService.getTimelineByIunTimelineId(Mockito.eq(iun), Mockito.anyString(), Mockito.eq(false))).thenReturn(new HashSet<>());

        boolean isNotificationCancellationRequested = timelineUtils.checkIsNotificationCancellationRequested(iun);
        Assertions.assertFalse(isNotificationCancellationRequested);
    }

    @Test
    void checkIsNotificationCancellationRequested() {
        String iun = "IUN-checkIsNotificationCancellationRequested";

        Set<TimelineElementInternal> setTimelineElement = new HashSet<>();

        String timelineEventId = TimelineEventId.NOTIFICATION_CANCELLATION_REQUEST.buildEventId(
                EventId.builder()
                        .iun(iun)
                        .build());

        TimelineElementInternal timelineElementInternal = TimelineElementInternal.builder()
                .category(TimelineElementCategoryInt.NOTIFICATION_CANCELLATION_REQUEST)
                .elementId(timelineEventId)
                .details(NotificationPaidDetailsInt.builder()
                        .build())
                .build();

        setTimelineElement.add(timelineElementInternal);

        Mockito.when(timelineService.getTimelineByIunTimelineId(iun, timelineEventId, false)).thenReturn(setTimelineElement);

        boolean isNotificationCancellationRequested = timelineUtils.checkIsNotificationCancellationRequested(iun);
        Assertions.assertTrue(isNotificationCancellationRequested);
    }
}