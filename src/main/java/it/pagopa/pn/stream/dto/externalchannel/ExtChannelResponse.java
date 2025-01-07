package it.pagopa.pn.stream.dto.externalchannel;

import it.pagopa.pn.stream.dto.address.PhysicalAddressInt;
import lombok.*;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class ExtChannelResponse {
    private String iun;
    private String eventId;
    private Instant notificationDate;
    private ResponseStatusInt responseStatus;
    private List<String> errorList;
    private List<String> attachmentKeys;
    private PhysicalAddressInt analogNewAddressFromInvestigation;
}
