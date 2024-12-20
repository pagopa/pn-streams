package it.pagopa.pn.stream.service.mapper;

import it.pagopa.pn.stream.dto.legalfacts.LegalFactCategoryInt;
import it.pagopa.pn.stream.dto.legalfacts.LegalFactsIdInt;
import it.pagopa.pn.stream.generated.openapi.server.webhook.v1.dto.LegalFactCategoryV20;
import it.pagopa.pn.stream.generated.openapi.server.webhook.v1.dto.LegalFactsIdV20;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LegalFactIdMapperTest {

    @Test
    void internalToExternal() {

        LegalFactsIdV20 actual = LegalFactIdMapper.internalToExternal(buildLegalFactsIdInt());

        Assertions.assertEquals(buildLegalFactsId(), actual);

    }

    private LegalFactsIdV20 buildLegalFactsId() {
        return LegalFactsIdV20.builder()
                .key("001")
                .category(LegalFactCategoryV20.ANALOG_DELIVERY)
                .build();
    }

    private LegalFactsIdInt buildLegalFactsIdInt() {
        return LegalFactsIdInt.builder()
                .key("001")
                .category(LegalFactCategoryInt.ANALOG_DELIVERY)
                .build();
    }
}