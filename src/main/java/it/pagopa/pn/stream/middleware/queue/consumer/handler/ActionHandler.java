package it.pagopa.pn.stream.middleware.queue.consumer.handler;


import it.pagopa.pn.commons.utils.MDCUtils;
import it.pagopa.pn.stream.middleware.queue.consumer.handler.utils.HandleEventUtils;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.WebhookAction;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.impl.WebhookActionsEventHandler;
import it.pagopa.pn.stream.utils.MdcKey;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;


@Configuration
@AllArgsConstructor
@CustomLog
public class ActionHandler {
    private final WebhookActionsEventHandler webhookActionsEventHandler;
    
    @Bean
    public Consumer<Message<WebhookAction>> pnStreamWebhookActionConsumer() {
        final String processName = "WEBHOOK ACTION";
        
        return message -> {
            try {
                MDC.put(MDCUtils.MDC_PN_CTX_TOPIC, MdcKey.WEBHOOK_KEY);

                log.debug("Handle action pnStreamWebhookActionConsumer, with content {}", message);log.debug("pnStreamWebhookActionConsumer, message={}", message);
                WebhookAction action = message.getPayload();
                HandleEventUtils.addIunToMdc(action.getIun());

                log.logStartingProcess(processName);
                webhookActionsEventHandler.handleEvent(action);
                log.logEndingProcess(processName);

                MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC);
            } catch (Exception ex) {
                log.logEndingProcess(processName, false, ex.getMessage());
                MDC.remove(MDCUtils.MDC_PN_CTX_TOPIC);
                HandleEventUtils.handleException(message.getHeaders(), ex);
                throw ex;
            }
        };
    }
}
