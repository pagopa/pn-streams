package it.pagopa.pn.stream.middleware.dao.dynamo;

import it.pagopa.pn.stream.middleware.dao.dynamo.entity.EventEntity;
import lombok.Data;

import java.util.List;

@Data
public class EventEntityBatch {
    private String streamId;
    private String lastEventIdRead;
    private List<EventEntity> events;
}
