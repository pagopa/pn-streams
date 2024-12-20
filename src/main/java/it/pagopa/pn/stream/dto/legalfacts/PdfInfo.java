package it.pagopa.pn.stream.dto.legalfacts;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PdfInfo {
    private String key;
    private int numberOfPages;
    private AarTemplateType aarTemplateType;
}
