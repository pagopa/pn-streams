package it.pagopa.pn.stream.middleware.dao.webhook.dynamo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@NoArgsConstructor
@Data
public class WebhookStreamEventCounter {

    public static final String COL_PK = "hashKey";
    public static final String COL_SK = "sortKey";
    public static final String COL_EVENT_CURRENT_COUNTER = "eventAtomicCounter";

    public WebhookStreamEventCounter(String paId, String streamId){
        this.setPaId(paId);
        this.setStreamId("COUNTER#"+streamId);
    }

    @Getter(onMethod=@__({@DynamoDbPartitionKey, @DynamoDbAttribute(COL_PK)})) private String paId;
    @Getter(onMethod=@__({@DynamoDbSortKey, @DynamoDbAttribute(COL_SK)}))  private String streamId;
    @Getter(onMethod=@__({@DynamoDbAttribute(COL_EVENT_CURRENT_COUNTER) })) private Long eventAtomicCounter;
}
