package it.pagopa.pn.stream.dto;

import it.pagopa.pn.stream.dto.address.PhysicalAddressInt;
import lombok.Getter;

@Getter
public enum ConfidentialInformationEnum {

    TAX_ID("taxId", "taxId", null, String.class),
    DENOMINATION("denomination", "denomination", null, String.class),
    NORMALIZED_ADDRESS("normalizedAddress", "newPhysicalAddress", null, PhysicalAddressInt.class),
    NEW_ADDRESS("newAddress", "newPhysicalAddress", null, PhysicalAddressInt.class),
    PHYSICAL_ADDRESS("physicalAddress", "physicalAddress", null, PhysicalAddressInt.class),
    OLD_ADDRESS("oldAddress", "physicalAddress", null, PhysicalAddressInt.class),
    FOUND_ADDRESS("foundAddress", "physicalAddress", null, PhysicalAddressInt.class),
    DIGITAL_ADDRESS("address","digitalAddress", "digitalAddress", String.class);

    private final String timelineKey;
    private final String confidentialValue;
    private final String parent;
    private final Class<?> type;


    ConfidentialInformationEnum(String timelineKey, String confidentialValue, String parent, Class<?> type) {
        this.timelineKey = timelineKey;
        this.confidentialValue = confidentialValue;
        this.parent = parent;
        this.type = type;
    }

}


