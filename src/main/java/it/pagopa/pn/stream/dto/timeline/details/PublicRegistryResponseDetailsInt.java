package it.pagopa.pn.stream.dto.timeline.details;

import it.pagopa.pn.stream.dto.address.LegalDigitalAddressInt;
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
public class PublicRegistryResponseDetailsInt implements RecipientRelatedTimelineElementDetails, DigitalAddressRelatedTimelineElement {
    private int recIndex;
    private LegalDigitalAddressInt digitalAddress;
    private PhysicalAddressInt physicalAddress;
    private String requestTimelineId;

    public String toLog() {
        return String.format(
                "recIndex=%d digitalAddress=%s physicalAddress=%s requestTimelineId=%s",
                recIndex,
                AuditLogUtils.SENSITIVE,
                AuditLogUtils.SENSITIVE,
                requestTimelineId
        );
    }
}