package it.pagopa.pn.stream.dto.timeline;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StatusInfoInternal {

    private String actual;
    private Instant statusChangeTimestamp;
    private boolean statusChanged;
}
