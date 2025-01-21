package it.pagopa.pn.stream.middleware.dao.dynamo;

import it.pagopa.pn.stream.middleware.dao.dynamo.entity.StreamNotificationEntity;
import reactor.core.publisher.Mono;

public interface StreamNotificationDao {

    Mono<StreamNotificationEntity> findByIun(String hashkey);

    Mono<StreamNotificationEntity> putItem(StreamNotificationEntity streamNotification);

}
