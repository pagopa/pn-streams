package it.pagopa.pn.stream.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Duration;

@Configuration
@ConfigurationProperties( prefix = "pn.stream")
@Data
@Import({SharedAutoConfiguration.class})
public class PnStreamConfigs {

    private Dao dao;
    private Topics topics;
    private String dataVaultBaseUrl;
    private Long scheduleInterval;
    private Integer maxLength;
    private Integer purgeDeletionWaittime;
    private Integer readBufferDelay;
    private Integer maxStreams;
    private Integer deltaCounter;
    private Duration ttl;
    private Duration disableTtl;
    private String firstVersion;
    private String currentVersion;

    @Data
    public static class Dao {
        private String streamsTableName;
        private String eventsTableName;
    }

    @Data
    public static class Topics {
        private String scheduledActions;
    }
}
