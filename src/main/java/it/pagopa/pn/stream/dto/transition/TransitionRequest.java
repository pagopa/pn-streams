package it.pagopa.pn.stream.dto.transition;

import it.pagopa.pn.stream.dto.ext.delivery.notification.status.NotificationStatusInt;
import it.pagopa.pn.stream.dto.timeline.details.TimelineElementCategoryInt;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransitionRequest {

    private NotificationStatusInt fromStatus;
    private TimelineElementCategoryInt timelineRowType;
    private boolean multiRecipient;
}
