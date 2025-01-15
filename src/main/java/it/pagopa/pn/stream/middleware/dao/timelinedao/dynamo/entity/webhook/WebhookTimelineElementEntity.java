package it.pagopa.pn.stream.middleware.dao.timelinedao.dynamo.entity.webhook;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.pagopa.pn.stream.dto.timeline.StatusInfoEntity;
import it.pagopa.pn.stream.utils.ObjectToStringDeserializer;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@DynamoDbBean
public class WebhookTimelineElementEntity {

    public static final String FIELD_IUN = "iun";
    public static final String FIELD_TIMELINE_ELEMENT_ID = "timelineElementId";

    private String iun;
    private String timelineElementId;
    private Instant timestamp;
    private String paId;
    private String category;
    private List<String> legalFactIds;
    @JsonDeserialize(using = ObjectToStringDeserializer.class)
    private String details;
    private StatusInfoEntity statusInfo;
    private Instant notificationSentAt;
    private Instant ingestionTimestamp;
    private Instant eventTimestamp;
    
    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = FIELD_IUN )
    public String getIun() {
        return iun;
    }
    public void setIun(String iun) {
        this.iun = iun;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute(value = FIELD_TIMELINE_ELEMENT_ID )
    public String getTimelineElementId() {
        return timelineElementId;
    }
    public void setTimelineElementId(String timelineElementId) {
        this.timelineElementId = timelineElementId;
    }

    @DynamoDbAttribute(value = "timestamp")
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    @DynamoDbAttribute(value = "paId")
    public String getPaId() {return paId;}
    public void setPaId(String paId) {this.paId = paId;}

    @DynamoDbAttribute(value = "category")
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    @DynamoDbAttribute(value = "legalFactId")
    public List<String> getLegalFactIds() {
        return legalFactIds;
    }
    public void setLegalFactIds(List<String> legalFactIds) {
        this.legalFactIds = legalFactIds;
    }
    
    @DynamoDbAttribute(value = "details") @DynamoDbIgnoreNulls
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }

    @DynamoDbAttribute(value = "statusInfo") @DynamoDbIgnoreNulls
    public StatusInfoEntity getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(StatusInfoEntity statusInfo) {
        this.statusInfo = statusInfo;
    }

    @DynamoDbAttribute(value = "notificationSentAt") @DynamoDbIgnoreNulls
    public Instant getNotificationSentAt() {
        return notificationSentAt;
    }

    public void setNotificationSentAt(Instant notificationSentAt) {
        this.notificationSentAt = notificationSentAt;
    }
    
    @DynamoDbAttribute(value = "ingestionTimestamp") @DynamoDbIgnoreNulls
    public Instant getIngestionTimestamp() {
        return ingestionTimestamp;
    }
    
    public void setIngestionTimestamp(Instant ingestionTimestamp) {
        this.ingestionTimestamp = ingestionTimestamp;
    }

    @DynamoDbAttribute(value = "eventTimestamp") @DynamoDbIgnoreNulls
    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Instant eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }
}

