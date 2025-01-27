package it.pagopa.pn.stream.middleware.dao.mapper;

import it.pagopa.pn.stream.dto.timeline.StatusInfoEntity;
import it.pagopa.pn.stream.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.middleware.dao.timelinedao.dynamo.entity.webhook.WebhookTimelineElementEntity;
import org.springframework.stereotype.Component;

@Component
public class DtoToEntityWebhookTimelineMapper {
    
    public WebhookTimelineElementEntity dtoToEntity(TimelineElementInternal dto) {
        return WebhookTimelineElementEntity.builder()
                .iun( dto.getIun() )
                .timelineElementId( dto.getElementId() )
                .paId( dto.getPaId() )
                .category(dto.getCategory())
                .details(dto.getDetails())
                .legalFactIds(dto.getLegalFactsIds())
                .statusInfo(dtoToStatusInfoEntity(dto.getStatusInfo()))
                .notificationSentAt(dto.getNotificationSentAt())
                .timestamp(dto.getBusinessTimestamp())
                .ingestionTimestamp(dto.getTimestamp())
                .eventTimestamp(dto.getBusinessTimestamp())
                .build();
    }

    private StatusInfoEntity dtoToStatusInfoEntity(StatusInfoInternal statusInfoInternal) {
        if(statusInfoInternal == null) return null;
        return StatusInfoEntity.builder()
                .statusChangeTimestamp(statusInfoInternal.getStatusChangeTimestamp())
                .statusChanged(statusInfoInternal.isStatusChanged())
                .actual(statusInfoInternal.getActual())
                .build();
    }
}
