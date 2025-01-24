package it.pagopa.pn.stream.dto.timeline;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class StatusInfoInternal {

    private final String actual;
    private final Instant statusChangeTimestamp;
    private final boolean statusChanged;
}
