package it.pagopa.pn.stream.middleware.dao.webhook;

import it.pagopa.pn.stream.middleware.dao.webhook.dynamo.entity.StreamEntity;
import it.pagopa.pn.stream.middleware.dao.webhook.dynamo.entity.WebhookStreamRetryAfter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface StreamEntityDao {

    String IMPLEMENTATION_TYPE_PROPERTY_NAME = "pn.middleware.impl.webhook-stream-dao";

    Flux<StreamEntity> findByPa(String paId);

    Mono<StreamEntity> get(String paId, String streamId);

    Mono<Tuple2<StreamEntity, WebhookStreamRetryAfter>> getWithRetryAfter(String paId, String streamId);

    Mono<Void> delete(String paId, String streamId);

    Mono<StreamEntity> save(StreamEntity entity);

    Mono<StreamEntity> update(StreamEntity entity);

    /**
     * Ritorna il nuovo valore del contatore.
     * Nel caso in cui la entity non sia presente, torna -1
     * @param streamEntity lo stream da aggiornare
     * @return nuovo id contatore
     */
    Mono<Long> updateAndGetAtomicCounter(StreamEntity streamEntity);

    Mono<StreamEntity> replaceEntity(StreamEntity replacedEntity, StreamEntity newEntity);

    Mono<StreamEntity> disable(StreamEntity entity);

    void updateStreamRetryAfter(WebhookStreamRetryAfter entity);
}
