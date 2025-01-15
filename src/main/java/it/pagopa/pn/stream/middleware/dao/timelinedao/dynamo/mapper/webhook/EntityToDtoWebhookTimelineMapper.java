package it.pagopa.pn.stream.middleware.dao.timelinedao.dynamo.mapper.webhook;

import it.pagopa.pn.stream.dto.timeline.StatusInfoEntity;
import it.pagopa.pn.stream.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.middleware.dao.timelinedao.dynamo.entity.webhook.WebhookTimelineElementEntity;
import org.springframework.stereotype.Component;

@Component
public class EntityToDtoWebhookTimelineMapper {
    
    public TimelineElementInternal entityToDto(WebhookTimelineElementEntity entity ) {

        return TimelineElementInternal.builder()
                .iun(entity.getIun())
                .elementId( entity.getTimelineElementId() )
                .category( entity.getCategory() )
                .details(entity.getDetails())
                .legalFactsIds(  entity.getLegalFactIds() )
                .statusInfo(entityToStatusInfoInternal(entity.getStatusInfo()))
                .notificationSentAt(entity.getNotificationSentAt())
                .paId(entity.getPaId())
                .timestamp(entity.getTimestamp())
                .ingestionTimestamp(entity.getIngestionTimestamp())
                .eventTimestamp(entity.getEventTimestamp())
                .build();
    }


    private StatusInfoInternal entityToStatusInfoInternal(StatusInfoEntity entity) {
        if(entity == null) return null;

        return StatusInfoInternal.builder()
                .actual(entity.getActual())
                .statusChanged(entity.isStatusChanged())
                .statusChangeTimestamp(entity.getStatusChangeTimestamp())
                .build();
    }
}
