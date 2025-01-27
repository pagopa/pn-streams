package it.pagopa.pn.stream.middleware.dao.dynamo;

import it.pagopa.pn.stream.middleware.dao.dynamo.entity.StreamEntity;
import it.pagopa.pn.stream.middleware.dao.dynamo.entity.StreamRetryAfter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Optional;

public interface StreamEntityDao {

    Flux<StreamEntity> findByPa(String paId);

    Mono<StreamEntity> get(String paId, String streamId);

    Mono<Tuple2<StreamEntity, Optional<StreamRetryAfter>>> getWithRetryAfter(String paId, String streamId);

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

    Mono<Void> updateStreamRetryAfter(StreamRetryAfter entity);
}
