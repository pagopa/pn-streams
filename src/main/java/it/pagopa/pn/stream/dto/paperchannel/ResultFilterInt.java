package it.pagopa.pn.stream.dto.paperchannel;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode
@Getter
@ToString
public class ResultFilterInt {

    private String fileKey;
    private String reasonCode;
    private String reasonDescription;
}
