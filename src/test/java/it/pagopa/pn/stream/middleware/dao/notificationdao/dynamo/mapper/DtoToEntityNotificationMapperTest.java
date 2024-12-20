package it.pagopa.pn.stream.middleware.dao.notificationdao.dynamo.mapper;

import it.pagopa.pn.stream.dto.notification.NotificationInternal;
import it.pagopa.pn.stream.middleware.dao.notificationdao.dynamo.entity.NotificationEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DtoToEntityNotificationMapperTest {

    @Test
    void dto2Entity_withValidNotification() {
        NotificationInternal notification = new NotificationInternal();
        notification.setHashkey("hashkey");
        notification.setGroups(Collections.singletonList("group1"));
        notification.setCreationDate(Instant.now());
        notification.setTtl(3600L);

        NotificationEntity entity = DtoToEntityNotificationMapper.dto2Entity(notification);

        assertEquals(notification.getHashkey(), entity.getHashkey());
        assertEquals(notification.getGroups(), entity.getGroups());
        assertEquals(notification.getCreationDate(), entity.getCreationDate());
        assertEquals(notification.getTtl(), entity.getTtl());
    }

    @Test
    void dto2Entity_withNullNotification() {
        NotificationInternal notification = null;

        NotificationEntity entity = DtoToEntityNotificationMapper.dto2Entity(notification);

        assertNull(entity);
    }

    @Test
    void dto2Entity_withEmptyNotification() {
        NotificationInternal notification = new NotificationInternal();

        NotificationEntity entity = DtoToEntityNotificationMapper.dto2Entity(notification);

        assertNull(entity.getHashkey());
        assertNull(entity.getGroups());
        assertNull(entity.getCreationDate());
        assertNull(entity.getTtl());
    }
}