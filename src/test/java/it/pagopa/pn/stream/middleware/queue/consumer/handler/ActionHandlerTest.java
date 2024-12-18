package it.pagopa.pn.stream.middleware.queue.consumer.handler;

import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.WebhookAction;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.webhookspool.impl.WebhookActionsEventHandler;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.function.Consumer;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class ActionHandlerTest {
    @InjectMocks
    private ActionHandler actionHandler;

    @Mock
    private WebhookActionsEventHandler webhookActionsEventHandler;


    @Test
    void pnStreamWebhookActionConsumer() {
        //GIVEN
        Message<WebhookAction> message = getWebhookActionMessage();
 
        //WHEN
        Consumer<Message<WebhookAction>> consumer = actionHandler.pnStreamWebhookActionConsumer();
        consumer.accept(message);

        //THEN
        WebhookAction action = message.getPayload();
        verify(webhookActionsEventHandler).handleEvent(action);
    }

    @NotNull
    private static Message<WebhookAction> getWebhookActionMessage() {
        return new Message<>() {
            @Override
            @NotNull
            public WebhookAction getPayload() {
                return WebhookAction.builder()
                        .iun("test")
                        .build();
            }

            @Override
            @NotNull
            public MessageHeaders getHeaders() {
                return new MessageHeaders(new HashMap<>());
            }
        };
    }

}