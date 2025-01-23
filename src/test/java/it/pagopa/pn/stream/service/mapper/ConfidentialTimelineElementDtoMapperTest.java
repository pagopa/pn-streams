package it.pagopa.pn.stream.service.mapper;

import it.pagopa.pn.stream.dto.address.PhysicalAddressInt;
import it.pagopa.pn.stream.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.AddressDto;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.AnalogDomicile;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConfidentialTimelineElementDtoMapperTest {


    @Test
    void externalToInternal() {

        ConfidentialTimelineElementDtoInt actual = ConfidentialTimelineElementDtoMapper.externalToInternal(buildConfidentialTimelineElementDto());

        Assertions.assertEquals(buildConfidentialTimelineElementDtoInt(), actual);
    }

    private ConfidentialTimelineElementDto buildConfidentialTimelineElementDto() {
        return ConfidentialTimelineElementDto.builder()
                .timelineElementId("001")
                .digitalAddress(AddressDto.builder().value("002").build())
                .physicalAddress(buildAnalogDomicile())
                .newPhysicalAddress(buildAnalogDomicile())
                .build();
    }

    private ConfidentialTimelineElementDtoInt buildConfidentialTimelineElementDtoInt() {
        return ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId("001")
                .digitalAddress("002")
                .physicalAddress(buildPhysicalAddressInt())
                .newPhysicalAddress(buildPhysicalAddressInt())
                .build();
    }

    private PhysicalAddressInt buildPhysicalAddressInt() {
        return PhysicalAddressInt.builder()
                .at("001")
                .address("002")
                .addressDetails("003")
                .zip("004")
                .municipality("005")
                .province("007")
                .foreignState("008").build();
    }

    private AnalogDomicile buildAnalogDomicile() {
        return AnalogDomicile.builder()
                .at("001")
                .address("002")
                .addressDetails("003")
                .cap("004")
                .municipality("005")
                .province("007")
                .state("008").build();
    }
}