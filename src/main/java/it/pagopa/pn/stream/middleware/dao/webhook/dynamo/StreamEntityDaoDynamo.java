package it.pagopa.pn.stream.middleware.dao.webhook.dynamo;

import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;

import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.stream.config.PnStreamConfigs;
import it.pagopa.pn.stream.middleware.dao.webhook.StreamEntityDao;
import it.pagopa.pn.stream.middleware.dao.webhook.dynamo.entity.StreamEntity;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

import it.pagopa.pn.stream.middleware.dao.webhook.dynamo.entity.WebhookStreamRetryAfter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@Component
@ConditionalOnProperty(name = StreamEntityDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.DYNAMO)
@Slf4j
public class StreamEntityDaoDynamo implements StreamEntityDao {

    private final DynamoDbAsyncTable<StreamEntity> table;
    private final DynamoDbAsyncTable<WebhookStreamRetryAfter> tableRetry;
    private final DynamoDbAsyncClient dynamoDbAsyncClient;
    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient;
    private final PnStreamConfigs pnStreamConfigs;

    public StreamEntityDaoDynamo(DynamoDbEnhancedAsyncClient dynamoDbEnhancedClient, DynamoDbAsyncClient dynamoDbAsyncClient, PnStreamConfigs cfg) {
       this.table = dynamoDbEnhancedClient.table(cfg.getWebhookDao().getStreamsTableName(), TableSchema.fromBean(StreamEntity.class));
       this.tableRetry = dynamoDbEnhancedClient.table(cfg.getWebhookDao().getStreamsTableName(), TableSchema.fromBean(WebhookStreamRetryAfter.class));
       this.dynamoDbAsyncClient = dynamoDbAsyncClient;
       this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
       this.pnStreamConfigs = cfg;
    }

    @Override
    public Flux<StreamEntity> findByPa(String paId) {
        log.debug("findByPa paId={}", paId);
        Key hashKey = Key.builder().partitionValue(paId).build();
        QueryConditional queryByHashKey = keyEqualTo( hashKey );
        return Flux.from(table.query(queryByHashKey).flatMapIterable(Page::items));
    }

    @Override
    public Mono<StreamEntity> get(String paId, String streamId) {
        log.info("get paId={} streamId={}", paId, streamId);
        Key hashKey = Key.builder().partitionValue(paId).sortValue(streamId).build();
        return Mono.fromFuture(table.getItem(hashKey));
    }

    @Override
    public Mono<Tuple2<StreamEntity, WebhookStreamRetryAfter>> getWithRetryAfter(String paId, String streamId) {
        log.info("getWithRetryAfter paId={} streamId={}", paId, streamId);
        Key hashKey = Key.builder().partitionValue(paId).sortValue(streamId).build();
        Key retryHashKey = Key.builder().partitionValue(paId).sortValue(WebhookStreamRetryAfter.RETRY_PREFIX+streamId).build();
        WebhookStreamRetryAfter retryEntity = new WebhookStreamRetryAfter();
        retryEntity.setPaId(paId);
        retryEntity.setStreamId(WebhookStreamRetryAfter.RETRY_PREFIX+streamId);
        retryEntity.setRetryAfter(Instant.now());

        ReadBatch streamEntityBatch = ReadBatch.builder(StreamEntity.class)
                .mappedTableResource(table)
                .addGetItem(hashKey)
                .build();
        ReadBatch streamRetryEntityBatch = ReadBatch.builder(WebhookStreamRetryAfter.class)
                .mappedTableResource(tableRetry)
                .addGetItem(retryHashKey)
                .build();

        Mono<BatchGetResultPage> deferred = Mono.defer(() ->
                Mono.from(dynamoDbEnhancedClient.batchGetItem(BatchGetItemEnhancedRequest.builder()
                        .readBatches(streamEntityBatch, streamRetryEntityBatch)
                        .build())));

        return deferred.flatMap(pages -> {
            List<StreamEntity> streamEntities = new ArrayList<>(pages.resultsForTable(table));
            List<WebhookStreamRetryAfter> streamRetryEntities = new ArrayList<>(pages.resultsForTable(tableRetry));
            return Mono.just(Tuples.of(
                    streamEntities.stream().filter(entity -> entity.getActivationDate() != null).toList().get(0),
                    streamRetryEntities.stream().filter(entity -> entity.getRetryAfter() != null).findFirst().orElse(retryEntity)));
        });
    }

    @Override
    public Mono<Void> delete(String paId, String streamId) {
        log.info("delete paId={} streamId={}", paId, streamId);
        Key hashKey = Key.builder().partitionValue(paId).sortValue(streamId).build();
        return Mono.fromFuture(table.deleteItem(hashKey)).then();
    }

    @Override
    public Mono<StreamEntity> save(StreamEntity entity) {
        log.info("save entity={}", entity);
        return Mono.fromFuture(table.putItem(entity).thenApply(r -> entity));
    }

    @Override
    public Mono<StreamEntity> update(StreamEntity entity) {

        UpdateItemEnhancedRequest<StreamEntity> updateItemEnhancedRequest =
                UpdateItemEnhancedRequest.builder(StreamEntity.class)
                        .item(entity)
                        .ignoreNulls(true)
                        .build();

        log.info("update stream entity={}", entity);
        return Mono.fromFuture(table.updateItem(updateItemEnhancedRequest).thenApply(r -> entity));
    }

    @Override
    public Mono<Long> updateAndGetAtomicCounter(StreamEntity streamEntity) {
        log.info("updateAndGetAtomicCounter paId={} streamId={} counter={}", streamEntity.getPaId(), streamEntity.getStreamId(), streamEntity.getEventAtomicCounter());
        // il metodo utilizza le primitive base di dynamodbclient per poter eseguire l'update
        // atomico tramite l'action "ADD" e facendosi ritornare il nuovo valore
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(StreamEntity.COL_PK, AttributeValue.builder().s(streamEntity.getPaId()).build());
        key.put(StreamEntity.COL_SK, AttributeValue.builder().s(streamEntity.getStreamId()).build());

        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName(table.tableName())
                .key(key)
                .returnValues(ReturnValue.UPDATED_NEW)
                .updateExpression("ADD " +StreamEntity.COL_EVENT_CURRENT_COUNTER + " :v")
                .expressionAttributeValues(Map.of(":v", AttributeValue.builder().n("1").build()))
                .conditionExpression("attribute_exists(" + StreamEntity.COL_PK + ")")
                .build();


        return Mono.fromFuture( dynamoDbAsyncClient.updateItem(updateRequest))
                .map(resp -> {
                    Long newcounter = Long.parseLong(resp.attributes().get(StreamEntity.COL_EVENT_CURRENT_COUNTER).n());
                    log.info("updateAndGetAtomicCounter done paId={} streamId={} newcounter={}", streamEntity.getPaId(), streamEntity.getStreamId(), newcounter);
                    return newcounter;
                }).onErrorResume(ConditionalCheckFailedException.class, e -> {
                    log.warn("updateAndGetAtomicCounter conditional failed, not updating counter and retourning -1");
                    return Mono.just(-1L);
                });
    }


    @Override
    public Mono<StreamEntity> replaceEntity(StreamEntity replacedEntity, StreamEntity newEntity){

        TransactUpdateItemEnhancedRequest<StreamEntity> updateRequest = TransactUpdateItemEnhancedRequest.builder(StreamEntity.class)
            .item(disableStream(replacedEntity))
            .ignoreNulls(true)
            .build();

        TransactPutItemEnhancedRequest<StreamEntity> createRequest = TransactPutItemEnhancedRequest.builder(StreamEntity.class)
            .item(newEntity)
            .build();


        TransactWriteItemsEnhancedRequest transactWriteItemsEnhancedRequest = TransactWriteItemsEnhancedRequest.builder()
            .addUpdateItem(table, updateRequest)
            .addPutItem(table, createRequest)
            .build();

        var f = dynamoDbEnhancedClient.transactWriteItems(transactWriteItemsEnhancedRequest);
        return Mono.fromFuture(f.thenApply(r->newEntity));
    }

    @Override
    public Mono<StreamEntity> disable(StreamEntity entity) {
        return update(disableStream(entity));
    }

    @Override
    public void updateStreamRetryAfter(WebhookStreamRetryAfter entity) {
        log.info("updateStreamRetryAfter entity={}", entity);
        tableRetry.putItem(entity);
    }

    private StreamEntity disableStream(StreamEntity streamEntity){
        streamEntity.setDisabledDate(Instant.now());
        streamEntity.setTtl(Instant.now().plus(pnStreamConfigs.getWebhook().getDisableTtl()).atZone(ZoneId.systemDefault()).toEpochSecond());
        streamEntity.setEventAtomicCounter(null);
        return streamEntity;
    }
}
