package it.pagopa.pn.stream.dto.timeline.details;

import it.pagopa.pn.stream.dto.address.PhysicalAddressInt;
import it.pagopa.pn.stream.utils.AuditLogUtils;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class AnalogSuccessWorkflowDetailsInt implements RecipientRelatedTimelineElementDetails, PhysicalAddressRelatedTimelineElement {
    private int recIndex;
    private PhysicalAddressInt physicalAddress;

    public String toLog() {
        return String.format(
                "recIndex=%d physicalAddress=%s",
                recIndex,
                AuditLogUtils.SENSITIVE
        );
    }
    
}
