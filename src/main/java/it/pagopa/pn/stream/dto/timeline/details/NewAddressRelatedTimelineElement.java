package it.pagopa.pn.stream.dto.timeline.details;

import it.pagopa.pn.stream.dto.address.PhysicalAddressInt;

public interface NewAddressRelatedTimelineElement extends ConfidentialInformationTimelineElement{
    PhysicalAddressInt getNewAddress();
    void setNewAddress(PhysicalAddressInt digitalAddressInt);
}
