package it.pagopa.pn.stream.middleware.dao.notificationdao.dynamo.mapper;

import it.pagopa.pn.stream.dto.notification.NotificationInternal;
import it.pagopa.pn.stream.middleware.dao.notificationdao.dynamo.entity.NotificationEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityToDtoNotificationMapper {
    public static NotificationInternal entity2Dto(NotificationEntity entity) {
        NotificationInternal dto = new NotificationInternal();
        if (entity == null) {
            return null;
        }
        dto.setHashkey(entity.getHashkey());
        dto.setGroups(entity.getGroups());
        dto.setCreationDate(entity.getCreationDate());
        dto.setTtl(entity.getTtl());
        return dto;
    }
}
