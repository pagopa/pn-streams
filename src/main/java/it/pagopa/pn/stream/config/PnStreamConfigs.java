package it.pagopa.pn.stream.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.util.List;

@Configuration
@ConfigurationProperties( prefix = "pn.stream")
@Data
@Import({SharedAutoConfiguration.class})
public class PnStreamConfigs {

    private String externalRegistryBaseUrl;

    private String performanceImprovementStartDate;

    private String performanceImprovementEndDate;

    private Webhook webhook;

    private WebhookDao webhookDao;

    private TimelinecounterDao timelinecounterDao;

    private List<String> listCategoriesPa;

    private Topics topics;

    private String deliveryBaseUrl;

    private LastPollForFutureActionDao lastPollForFutureActionDao;

    private String dataVaultBaseUrl;

    private TimelineDao timelineDao;

    @Data
    public static class TimelineDao {
        private String tableName;
    }

    @Data
    public static class TimelinecounterDao {
        private String tableName;
    }


    @Data
    public static class Webhook {
        private Long scheduleInterval;
        private Integer maxLength;
        private Integer purgeDeletionWaittime;
        private Integer readBufferDelay;
        private Integer maxStreams;
        //Delta utilizzato per il counter di uno stream di sostituzione
        private Integer deltaCounter;
        private Duration ttl;
        private Duration disableTtl;
        private String firstVersion;
        private String currentVersion;
    }


    @Data
    public static class WebhookDao {
        private String streamsTableName;
        private String eventsTableName;
    }

    @Data
    public static class Topics {

        private String newNotifications;

        private String scheduledActions;

        private String executedActions;

        private String toExternalChannelPec;

        private String toExternalChannelEmail;

        private String toExternalChannelPaper;

        private String fromExternalChannel;

        private String safeStorageEvents;

        private String nationalRegistriesEvents;

        private String addressManagerEvents;

        private String f24Events;

        private String deliveryValidationEvents;
    }

    @Data
    public static class LastPollForFutureActionDao {
        private String tableName;
        private String lockTableName;
    }
}
