package it.pagopa.pn.stream.dto.legalfacts;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AARInfo {
    private byte[] bytesArrayGeneratedAar;
    private AarTemplateType templateType;
}
