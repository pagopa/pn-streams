package it.pagopa.pn.stream.service.mapper;

import it.pagopa.pn.stream.dto.address.PhysicalAddressInt;
import it.pagopa.pn.stream.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.AnalogDomicile;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;

public class ConfidentialTimelineElementDtoMapper {
    private ConfidentialTimelineElementDtoMapper() {
    }

    public static ConfidentialTimelineElementDtoInt externalToInternal(ConfidentialTimelineElementDto dtoExt) {
        ConfidentialTimelineElementDtoInt.ConfidentialTimelineElementDtoIntBuilder dtoIntBuilder = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId(dtoExt.getTimelineElementId())
                .taxId(dtoExt.getTaxId())
                .denomination(dtoExt.getDenomination());

        if (dtoExt.getDigitalAddress() != null) {
            dtoIntBuilder.digitalAddress(dtoExt.getDigitalAddress().getValue());
        }

        AnalogDomicile physicalAddress = dtoExt.getPhysicalAddress();
        if (physicalAddress != null) {
            dtoIntBuilder.physicalAddress(
                    PhysicalAddressInt.builder()
                            .address(physicalAddress.getAddress())
                            .addressDetails(physicalAddress.getAddressDetails())
                            .at(physicalAddress.getAt())
                            .municipality(physicalAddress.getMunicipality())
                            .municipalityDetails(physicalAddress.getMunicipalityDetails())
                            .zip(physicalAddress.getCap())
                            .foreignState(physicalAddress.getState())
                            .province(physicalAddress.getProvince())
                            .build()
            );
        }

        AnalogDomicile newPhysicalAddress = dtoExt.getNewPhysicalAddress();
        if (newPhysicalAddress != null) {
            dtoIntBuilder.newPhysicalAddress(
                    PhysicalAddressInt.builder()
                            .address(newPhysicalAddress.getAddress())
                            .addressDetails(newPhysicalAddress.getAddressDetails())
                            .at(newPhysicalAddress.getAt())
                            .municipality(newPhysicalAddress.getMunicipality())
                            .municipalityDetails(newPhysicalAddress.getMunicipalityDetails())
                            .zip(newPhysicalAddress.getCap())
                            .foreignState(newPhysicalAddress.getState())
                            .province(newPhysicalAddress.getProvince())
                            .build()
            );
        }

        return dtoIntBuilder.build();
    }

}
