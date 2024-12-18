package it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool;

public interface WebhooksPool {
     void scheduleFutureAction(WebhookAction action);
}
