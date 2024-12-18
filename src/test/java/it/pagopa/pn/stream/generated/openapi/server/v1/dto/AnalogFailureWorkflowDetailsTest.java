package it.pagopa.pn.stream.generated.openapi.server.v1.dto;

import it.pagopa.pn.stream.generated.openapi.server.webhook.v1.dto.AnalogFailureWorkflowDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnalogFailureWorkflowDetailsTest {

    private AnalogFailureWorkflowDetails details;

    @BeforeEach
    void setUp() {
        details = new AnalogFailureWorkflowDetails();
        details.setRecIndex(1);
    }

    @Test
    void getRecIndex() {
        Assertions.assertEquals(1, details.getRecIndex());
    }

    @Test
    void testEquals() {
        AnalogFailureWorkflowDetails expected = AnalogFailureWorkflowDetails.builder()
                .recIndex(1)
                .build();

        Assertions.assertEquals(Boolean.TRUE, details.equals(expected));
    }
}