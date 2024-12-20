package it.pagopa.pn.stream.dto.timeline.details;

import it.pagopa.pn.stream.dto.address.LegalDigitalAddressInt;
import it.pagopa.pn.stream.utils.AuditLogUtils;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.EndWorkflowStatus;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class DigitalDeliveryCreationRequestDetailsInt implements RecipientRelatedTimelineElementDetails, DigitalAddressRelatedTimelineElement{
    private int recIndex;
    private EndWorkflowStatus endWorkflowStatus;
    private Instant completionWorkflowDate;
    private LegalDigitalAddressInt digitalAddress;
    private String legalFactId;

    public String toLog() {
        return String.format(
                "recIndex=%d endWorkflowStatus%s completionWorkflowDate=%s digitalAddress=%s legalFactId=%s",
                recIndex,
                endWorkflowStatus,
                completionWorkflowDate,
                AuditLogUtils.SENSITIVE,
                legalFactId
        );
    }
}