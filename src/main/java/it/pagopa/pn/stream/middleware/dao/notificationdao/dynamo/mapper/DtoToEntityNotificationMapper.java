package it.pagopa.pn.stream.middleware.dao.notificationdao.dynamo.mapper;

import it.pagopa.pn.stream.dto.notification.NotificationInternal;
import it.pagopa.pn.stream.middleware.dao.notificationdao.dynamo.entity.NotificationEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DtoToEntityNotificationMapper {
    public static NotificationEntity dto2Entity(NotificationInternal notification) {
        NotificationEntity entity = new NotificationEntity();
        if(notification == null) {
            return null;
        }
        entity.setHashkey(notification.getHashkey());
        entity.setGroups(notification.getGroups());
        entity.setCreationDate(notification.getCreationDate());
        entity.setTtl(notification.getTtl());
        return entity;
    }
}
