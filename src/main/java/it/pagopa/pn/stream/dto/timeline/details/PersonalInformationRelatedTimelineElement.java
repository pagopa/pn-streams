package it.pagopa.pn.stream.dto.timeline.details;

public interface PersonalInformationRelatedTimelineElement extends ConfidentialInformationTimelineElement{
    String getTaxId();
    void setTaxId(String taxId);

    String getDenomination();
    void setDenomination(String denomination);
}
