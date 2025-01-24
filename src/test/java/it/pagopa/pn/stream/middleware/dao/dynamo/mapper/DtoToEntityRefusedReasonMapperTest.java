package it.pagopa.pn.stream.middleware.dao.dynamo.mapper;

import it.pagopa.pn.stream.generated.openapi.server.v1.dto.RefusedReason;
import it.pagopa.pn.stream.middleware.dao.dynamo.entity.RefusedReasonEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DtoToEntityRefusedReasonMapperTest {
    public static final String ERROR_CODE = "FILE_NOTFOUND";
    public static final String DETAIL = "Allegato non trovato. fileKey=81dde2a8-9719-4407-b7b3-63e7ea694869";

    @Test
    void dtoToEntity() {
        RefusedReason refusedReason = new RefusedReason();
        refusedReason.setErrorCode( ERROR_CODE );
        refusedReason.setDetail( DETAIL );

        RefusedReasonEntity refusedReasonEntity = DtoToEntityRefusedReasonMapper.dtoToEntity( refusedReason );

        Assertions.assertEquals( ERROR_CODE, refusedReasonEntity.getErrorCode() );
        Assertions.assertEquals( DETAIL, refusedReasonEntity.getDetail() );
    }

}
