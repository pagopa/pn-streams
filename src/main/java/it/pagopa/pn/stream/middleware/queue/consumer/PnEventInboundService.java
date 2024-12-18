/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.pagopa.pn.stream.middleware.queue.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.utils.MDCUtils;
import it.pagopa.pn.stream.config.PnStreamConfigs;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.impl.WebhookActionEventType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.cloud.function.context.MessageRoutingCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static it.pagopa.pn.stream.exceptions.PnStreamExceptionCodes.*;

@Configuration
@Slf4j
public class PnEventInboundService {
    private final EventHandler eventHandler;
    private final String externalChannelEventQueueName;
    private final String safeStorageEventQueueName;
    private final String nationalRegistriesEventQueueName;
    private final String addressManagerEventQueueName;
    private final String validateF24EventQueueName;
    private final String deliveryValidationEvents;

    public PnEventInboundService(EventHandler eventHandler, PnStreamConfigs cfg) {
        this.eventHandler = eventHandler;
        this.externalChannelEventQueueName = cfg.getTopics().getFromExternalChannel();
        this.safeStorageEventQueueName = cfg.getTopics().getSafeStorageEvents();
        this.nationalRegistriesEventQueueName = cfg.getTopics().getNationalRegistriesEvents();
        this.addressManagerEventQueueName = cfg.getTopics().getAddressManagerEvents();
        this.validateF24EventQueueName = cfg.getTopics().getF24Events();
        this.deliveryValidationEvents = cfg.getTopics().getDeliveryValidationEvents();
    }

    //Viene definita un implementazione (anonima) di MessageRoutingCallback. Nel contesto di Spring, quando viene ricevuto un messaggio da una coda gestita da Spring Cloud Stream,
    // il framework cerca un bean che implementa l'interfaccia MessageRoutingCallback, per richiamarne il metodo routingResult, che dovrà fornirne il nome del bean che gestisce quello
    // specifico messaggio, dunque l'handler per quel messaggio. Spring utilizza il nome del bean per cercare all'interno del proprio contesto  e recuperare l'istanza del bean corrispondente.
    // Questo avviene attraverso il "BeanFactory" di Spring.
    @Bean
    public MessageRoutingCallback customRouter() {
        return new MessageRoutingCallback() {
            @Override
            public FunctionRoutingResult routingResult(Message<?> message) {
                setMdc(message);
                return new FunctionRoutingResult(handleMessage(message));
            }
        };
    }

    private void setMdc(Message<?> message) {
        MessageHeaders messageHeaders = message.getHeaders();
        MDCUtils.clearMDCKeys();
        
        if (messageHeaders.containsKey("aws_messageId")){
            String awsMessageId = messageHeaders.get("aws_messageId", String.class);
            MDC.put(MDCUtils.MDC_PN_CTX_MESSAGE_ID, awsMessageId);
        }
        
        if (messageHeaders.containsKey("X-Amzn-Trace-Id")){
            String traceId = messageHeaders.get("X-Amzn-Trace-Id", String.class);
            MDC.put(MDCUtils.MDC_TRACE_ID_KEY, traceId);
        } else {
            MDC.put(MDCUtils.MDC_TRACE_ID_KEY, String.valueOf(UUID.randomUUID()));
        }

        String iun = (String) message.getHeaders().get("iun");
        if(iun != null){
            MDC.put(MDCUtils.MDC_PN_IUN_KEY, iun);
        }
    }

    private String handleMessage(Message<?> message) {
        String eventType = (String) message.getHeaders().get("eventType");
        log.debug("Received message from customRouter with eventType={}", eventType);

        String iun = (String) message.getHeaders().get("iun");

        if (eventType != null) {
            //Se l'event type e valorizzato ...
            if (WebhookActionEventType.WEBHOOK_ACTION_GENERIC.name().equals(eventType)) {
                //... e si tratta di una WEBHOOK ACTION, viene gestito con l'handleWebhookAction
                return handleWebhookAction();
            }
        }else {
            //Se l'eventType non è valorizzato entro sicuramente qui
            eventType = handleOtherEvent(message);
        }

        /*... arrivati qui, l'eventType o già valorizzato MA non era: ACTION_GENERIC, WEBHOOK_ACTION_GENERIC, EXTERNAL_CHANNELS_EVENT
            dunque rientrano i casi di NEW_NOTIFICATION, NOTIFICATION_VIEWED, NOTIFICATION_PAID ecc. 
            oppure l'eventType non era valorizzato ed è stato valorizzato in handleExternalChannelEvent.
         */

        String handlerName = eventHandler.getHandler().get(eventType);
        if (!StringUtils.hasText(handlerName)) {
            log.error("undefined handler for eventType={}", eventType);
        }

        log.debug("Handler for eventType={} is {} - iun={}", eventType, handlerName, iun);

        return handlerName;
    }

    @NotNull
    private String handleOtherEvent(Message<?> message) {
        log.error("eventType not present, cannot start scheduled action headers={} payload={}", message.getHeaders(), message.getPayload());
        throw new PnInternalException("eventType not present, cannot start scheduled action", ERROR_CODE_STREAM_EVENTTYPENOTSUPPORTED);
    }

    @NotNull
    private String handleWebhookAction() {
        return "pnStreamWebhookActionConsumer";
    }

    private String handleGenericAction(Message<?> message) {
        /*Quando verrà utilizzata la sola versione v2 verificare se si può evitare di dover gestire la action in modo separato, valorizzando direttamente in fase
            di scheduling l'eventType con il valore del type della action (ActionPoolImpl -> addToActionsQueue)
         */
        Map<String, String> actionMap = getActionMapFromMessage(message);
        String actionType = actionMap.get("type");
        if (actionType != null) {
            String handlerName = eventHandler.getHandler().get(actionType);
            if (!StringUtils.hasText(handlerName)) {
                log.error("undefined handler for actionType={}", actionType);
            }
            return handlerName;
        } else {
            log.error("actionType not present, cannot start scheduled action");
            throw new PnInternalException("actionType not present, cannot start scheduled action", ERROR_CODE_STREAM_ACTIONTYPENOTSUPPORTED);
        }
    }

    private Map<String, String> getActionMapFromMessage(Message<?> message) {
        try {
            String payload = (String) message.getPayload();
            Map<String, String> action = new ObjectMapper().readValue(payload, HashMap.class);

            if (action == null) {
                log.error("Action is not valid, cannot start scheduled action");
                throw new PnInternalException("Action is not valid, cannot start scheduled action", ERROR_CODE_STREAM_ACTIONEXCEPTION);
            }
            return action;
        } catch (JsonProcessingException ex) {
            log.error("Exception during json mapping ex", ex);
            throw new PnInternalException("Exception during json mapping ex=" + ex, ERROR_CODE_STREAM_ACTIONEXCEPTION);
        }
    }

}
