package it.pagopa.pn.stream.dto.timeline.details;

import it.pagopa.pn.stream.dto.address.PhysicalAddressInt;

public interface PhysicalAddressRelatedTimelineElement extends ConfidentialInformationTimelineElement{
    PhysicalAddressInt getPhysicalAddress();
    void setPhysicalAddress(PhysicalAddressInt physicalAddressInt);
}
