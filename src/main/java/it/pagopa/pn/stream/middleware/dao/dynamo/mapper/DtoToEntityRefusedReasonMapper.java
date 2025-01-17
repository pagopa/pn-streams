package it.pagopa.pn.stream.middleware.dao.dynamo.mapper;

import it.pagopa.pn.stream.generated.openapi.server.v1.dto.RefusedReason;
import it.pagopa.pn.stream.middleware.dao.dynamo.entity.RefusedReasonEntity;
import org.springframework.stereotype.Component;

@Component
public class DtoToEntityRefusedReasonMapper {

    private DtoToEntityRefusedReasonMapper(){}

    public static RefusedReasonEntity dtoToEntity(RefusedReason refusedReason) {
        RefusedReasonEntity refusedReasonEntity = new RefusedReasonEntity();
        refusedReasonEntity.setErrorCode( refusedReason.getErrorCode() );
        refusedReasonEntity.setDetail( refusedReason.getDetail() );
        return refusedReasonEntity;
    }
}
