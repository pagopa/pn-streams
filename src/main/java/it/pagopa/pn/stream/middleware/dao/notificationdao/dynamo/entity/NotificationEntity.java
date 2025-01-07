package it.pagopa.pn.stream.middleware.dao.notificationdao.dynamo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;
import java.util.List;

@DynamoDbBean
@NoArgsConstructor
@Data
public class NotificationEntity {
    public static final String COL_PK = "hashkey";
    public static final String COL_GROUPS = "groups";
    public static final String COL_CREATION_DATE = "creationDate";
    public static final String COL_TTL = "ttl";

    @Getter(onMethod=@__({@DynamoDbPartitionKey, @DynamoDbAttribute(COL_PK)})) private String hashkey;
    @Getter(onMethod=@__({@DynamoDbAttribute(COL_GROUPS)})) private List<String> groups;
    @Getter(onMethod=@__({@DynamoDbAttribute(COL_CREATION_DATE)})) private Instant creationDate;
    @Getter(onMethod=@__({@DynamoDbAttribute(COL_TTL)})) private Long ttl;
    
}

