package it.pagopa.pn.stream.middleware.dao;

import it.pagopa.pn.stream.BaseTest;
import it.pagopa.pn.stream.middleware.dao.dynamo.StreamNotificationDaoImpl;
import it.pagopa.pn.stream.middleware.dao.dynamo.entity.StreamNotificationEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

class StreamNotificationDaoIT extends BaseTest.WithLocalStack {

    @Autowired
    StreamNotificationDaoImpl streamNotificationDaoImpl;

    @Test
    void putAndGetItem(){
        StreamNotificationEntity entity = new StreamNotificationEntity();
        entity.setTtl(Instant.now().plusSeconds(7889400).getEpochSecond());
        entity.setHashKey("iun");
        entity.setGroup("group");
        entity.setCreationDate(Instant.now());
        streamNotificationDaoImpl.putItem(entity).block();
        StreamNotificationEntity response = streamNotificationDaoImpl.findByIun("iun").block();
        assert response != null;
        Assertions.assertEquals(entity.getTtl(), response.getTtl());
        Assertions.assertEquals(entity.getHashKey(), response.getHashKey());
        Assertions.assertEquals(entity.getGroup(), response.getGroup());
        Assertions.assertEquals(entity.getCreationDate(), response.getCreationDate());
    }

}
