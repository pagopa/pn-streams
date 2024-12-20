package it.pagopa.pn.stream.middleware.dao.notificationdao.dynamo.mapper;

import it.pagopa.pn.stream.dto.notification.NotificationInternal;
import it.pagopa.pn.stream.middleware.dao.notificationdao.dynamo.entity.NotificationEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EntityToDtoNotificationMapperTest {

    @Test
    void entity2Dto_withValidEntity() {
        NotificationEntity entity = new NotificationEntity();
        entity.setHashkey("hashkey");
        entity.setGroups(Collections.singletonList("group1"));
        entity.setCreationDate(Instant.now());
        entity.setTtl(3600L);

        NotificationInternal dto = EntityToDtoNotificationMapper.entity2Dto(entity);

        assertEquals(entity.getHashkey(), dto.getHashkey());
        assertEquals(entity.getGroups(), dto.getGroups());
        assertEquals(entity.getCreationDate(), dto.getCreationDate());
        assertEquals(entity.getTtl(), dto.getTtl());
    }

    @Test
    void entity2Dto_withNullEntity() {
        NotificationEntity entity = null;

        NotificationInternal dto = EntityToDtoNotificationMapper.entity2Dto(entity);

        assertNull(dto);
    }

    @Test
    void entity2Dto_withEmptyEntity() {
        NotificationEntity entity = new NotificationEntity();

        NotificationInternal dto = EntityToDtoNotificationMapper.entity2Dto(entity);

        assertNull(dto.getHashkey());
        assertNull(dto.getGroups());
        assertNull(dto.getCreationDate());
        assertNull(dto.getTtl());
    }
}