package it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.impl;

import it.pagopa.pn.api.dto.events.IEventType;

public enum StreamActionEventType implements IEventType {
    WEBHOOK_ACTION_GENERIC( StreamEvent.class );

    private final Class<?> eventClass;

    StreamActionEventType(Class<?> eventClass) {
        this.eventClass = eventClass;
    }

    public Class<?> getEventJavaClass() {
        return eventClass;
    }

}
