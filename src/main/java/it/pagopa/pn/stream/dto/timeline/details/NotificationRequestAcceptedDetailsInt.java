package it.pagopa.pn.stream.dto.timeline.details;

import it.pagopa.pn.stream.utils.AuditLogUtils;
import lombok.*;


@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class NotificationRequestAcceptedDetailsInt implements TimelineElementDetailsInt{

    public String toLog() {
        return AuditLogUtils.EMPTY;
    }
}
