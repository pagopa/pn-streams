package it.pagopa.pn.stream.dto.timeline.details;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class SendingReceipt {
    private String id;
    private String system;
}
