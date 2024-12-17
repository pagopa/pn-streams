package it.pagopa.pn.stream.middleware.dao.notificationdao;

import it.pagopa.pn.stream.exceptions.PnStreamException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

@Slf4j
public abstract class BaseDao<T> {
    private final DynamoDbAsyncTable<T> tableAsync;

    protected BaseDao(
            DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
            String tableName,
            Class<T> entityClass
    ) {
        this.tableAsync = dynamoDbEnhancedAsyncClient.table(tableName, TableSchema.fromBean(entityClass));
    }

    protected Mono<T> putItem(PutItemEnhancedRequest<T> request, String hashkey) {
        return Mono.fromFuture(tableAsync.putItem(request))
                .thenReturn(request.item())
                .onErrorResume(ConditionalCheckFailedException.class, e -> {
                    log.warn("Conditional check failed: {}", e.getMessage());
                    return Mono.error((new PnStreamException(String.format("NotificationDao doesn't exist for hashkey: [{%s}]", hashkey),
                            400, "ConditionalCheckFailedException")));
                });
    }

    protected Mono<T> getItem(String hashkey) {
        return Mono.fromFuture(tableAsync.getItem(Key.builder().partitionValue(hashkey).build()))
                .onErrorResume(ConditionalCheckFailedException.class, e -> {
                    log.warn("Conditional check failed: {}", e.getMessage());
                    return Mono.error((new PnStreamException(String.format("NotificationDao doesn't exist for hashkey: [{%s}]", hashkey),
                            400, "ConditionalCheckFailedException")));
                });
    }

    protected String buildExistingConditionExpression(boolean attributeHasToExist, String attribute) {
        if (attributeHasToExist) {
            return "attribute_exists(" + attribute + ")";
        }
        return "attribute_not_exists(" + attribute + ")";
    }



}

