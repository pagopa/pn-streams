package it.pagopa.pn.stream;

import it.pagopa.pn.stream.config.PnStreamConfigs;
import it.pagopa.pn.stream.config.PnStreamSchedulingConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

class PnStreamSchedulingConfigurationTest {

    private PnStreamSchedulingConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = new PnStreamSchedulingConfiguration();
    }

    @Test
    void lockProvider() {
        DynamoDbClient dynamoDB = DynamoDbClient.builder().build();
        PnStreamConfigs cfg = new PnStreamConfigs();
        PnStreamConfigs.LastPollForFutureActionDao dao = new PnStreamConfigs.LastPollForFutureActionDao();
        dao.setLockTableName("Lock");
        cfg.setLastPollForFutureActionDao(dao);
        LockProvider provider = configuration.lockProvider(dynamoDB, cfg);
        Assertions.assertNotNull(provider);
    }

}