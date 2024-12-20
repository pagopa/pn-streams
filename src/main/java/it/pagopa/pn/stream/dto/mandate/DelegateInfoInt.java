package it.pagopa.pn.stream.dto.mandate;

import it.pagopa.pn.stream.dto.ext.datavault.RecipientTypeInt;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class DelegateInfoInt {
    private String internalId;
    private String taxId;
    private String operatorUuid;
    private String mandateId;
    private String denomination;
    private RecipientTypeInt delegateType;
}
