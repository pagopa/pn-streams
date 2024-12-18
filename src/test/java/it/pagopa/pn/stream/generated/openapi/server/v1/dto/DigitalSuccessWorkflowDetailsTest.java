package it.pagopa.pn.stream.generated.openapi.server.v1.dto;

import it.pagopa.pn.stream.generated.openapi.server.webhook.v1.dto.DigitalAddress;
import it.pagopa.pn.stream.generated.openapi.server.webhook.v1.dto.DigitalSuccessWorkflowDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DigitalSuccessWorkflowDetailsTest {

    private DigitalSuccessWorkflowDetails details;

    @BeforeEach
    void setUp() {
        DigitalAddress address = DigitalAddress.builder().address("aa").type("test").build();
        details = new DigitalSuccessWorkflowDetails();
        details.digitalAddress(address);
        details.setRecIndex(1);
    }

    @Test
    void getRecIndex() {
        Assertions.assertEquals(1, details.getRecIndex());
    }

    @Test
    void digitalAddress() {
        DigitalAddress address = DigitalAddress.builder().address("aa").type("test").build();

        DigitalSuccessWorkflowDetails data = DigitalSuccessWorkflowDetails.builder()
                .recIndex(1)
                .digitalAddress(address)
                .build();

        Assertions.assertEquals(data, details);
    }

    @Test
    void getDigitalAddress() {
        DigitalAddress address = DigitalAddress.builder().address("aa").type("test").build();
        Assertions.assertEquals(address, details.getDigitalAddress());
    }

    @Test
    void testEquals() {
        DigitalAddress address = DigitalAddress.builder().address("aa").type("test").build();

        DigitalSuccessWorkflowDetails data = DigitalSuccessWorkflowDetails.builder()
                .recIndex(1)
                .digitalAddress(address)
                .build();
        Assertions.assertEquals(Boolean.TRUE, details.equals(data));
    }
    
}