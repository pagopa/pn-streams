package it.pagopa.pn.stream.dto.timeline;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TimelineElementInternal{
    private String iun;
    private String elementId;
    private Instant timestamp;
    private String paId;
    private List<String> legalFactsIds;
    private String category;
    private String details;
    private StatusInfoInternal statusInfo;
    private Instant notificationSentAt;
    private Instant ingestionTimestamp; //Questo campo viene valorizzato solo ed esclusivamente in uscita per api e webhook dal mapper
    private Instant eventTimestamp; //Questo campo viene valorizzato solo ed esclusivamente in uscita per api e webhook dal mapper
}
