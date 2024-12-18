package it.pagopa.pn.stream.middleware;


import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.stream.config.PnStreamConfigs;
import it.pagopa.pn.stream.middleware.queue.producer.webhook.sqs.SqsWebhookProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class PnStreamMiddlewareConfigs {

    private final PnStreamConfigs cfg;

    public PnStreamMiddlewareConfigs(PnStreamConfigs cfg) {
        this.cfg = cfg;
    }

    @Bean
    public SqsWebhookProducer webhookActionsEventProducer(SqsClient sqs, ObjectMapper objMapper) {
        return new SqsWebhookProducer( sqs, cfg.getTopics().getScheduledActions(), objMapper);
    }
}

