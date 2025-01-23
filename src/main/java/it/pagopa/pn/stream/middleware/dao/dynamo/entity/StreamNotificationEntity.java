package it.pagopa.pn.stream.middleware.dao.dynamo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@DynamoDbBean
@NoArgsConstructor
@Data
public class StreamNotificationEntity {
    public static final String COL_PK = "hashKey";
    public static final String COL_GROUP = "group";
    public static final String COL_CREATION_DATE = "creationDate";
    public static final String COL_TTL = "ttl";

    @Getter(onMethod=@__({@DynamoDbPartitionKey, @DynamoDbAttribute(COL_PK)})) private String hashKey;
    @Getter(onMethod=@__({@DynamoDbAttribute(COL_GROUP)})) private String group;
    @Getter(onMethod=@__({@DynamoDbAttribute(COL_CREATION_DATE)})) private Instant creationDate;
    @Getter(onMethod=@__({@DynamoDbAttribute(COL_TTL)})) private Long ttl;
    
}

