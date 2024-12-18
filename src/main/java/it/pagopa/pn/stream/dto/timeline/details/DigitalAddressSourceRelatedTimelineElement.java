package it.pagopa.pn.stream.dto.timeline.details;

import it.pagopa.pn.stream.dto.address.DigitalAddressSourceInt;

public interface DigitalAddressSourceRelatedTimelineElement  extends RecipientRelatedTimelineElementDetails {
    DigitalAddressSourceInt getDigitalAddressSource();
    void setDigitalAddressSource(DigitalAddressSourceInt digitalAddressInt);
}
