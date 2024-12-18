package it.pagopa.pn.stream.generated.openapi.server.v1.dto;

import it.pagopa.pn.stream.generated.openapi.server.webhook.v1.dto.AnalogSuccessWorkflowDetails;
import it.pagopa.pn.stream.generated.openapi.server.webhook.v1.dto.PhysicalAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnalogSuccessWorkflowDetailsTest {

    private AnalogSuccessWorkflowDetails details;

    @BeforeEach
    void setUp() {
        details = new AnalogSuccessWorkflowDetails();
        details.setRecIndex(1);
        details.setPhysicalAddress(PhysicalAddress.builder()
                .address("001")
                .build());
    }

    @Test
    void getRecIndex() {
        Assertions.assertEquals(1, details.getRecIndex());
    }

    @Test
    void getPhysicalAddress() {
        PhysicalAddress address = PhysicalAddress.builder()
                .address("001")
                .build();

        Assertions.assertEquals(address, details.getPhysicalAddress());
    }

    @Test
    void testEquals() {
        AnalogSuccessWorkflowDetails expected = AnalogSuccessWorkflowDetails.builder()
                .recIndex(1)
                .physicalAddress(PhysicalAddress.builder()
                        .address("001")
                        .build())
                .build();

        Assertions.assertEquals(Boolean.TRUE, details.equals(expected));
    }
    
}