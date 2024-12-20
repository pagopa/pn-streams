package it.pagopa.pn.stream.dto.legalfacts;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class LegalFactsIdInt {
    private String key;
    private LegalFactCategoryInt category;
}
