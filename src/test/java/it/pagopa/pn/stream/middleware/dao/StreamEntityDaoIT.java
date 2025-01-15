package it.pagopa.pn.stream.middleware.dao;

import it.pagopa.pn.stream.BaseTest;
import it.pagopa.pn.stream.middleware.dao.dynamo.StreamEntityDao;
import it.pagopa.pn.stream.middleware.dao.dynamo.entity.StreamEntity;
import it.pagopa.pn.stream.middleware.dao.dynamo.entity.WebhookStreamRetryAfter;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import reactor.util.function.Tuple2;

import java.time.Instant;
import java.util.Optional;

public class StreamEntityDaoIT extends BaseTest.WithLocalStack {

    @Autowired
    StreamEntityDao streamEntityDaoDynamo;

    @Test
    void findByPa_returnsFluxOfStreamEntities() {
        streamEntityDaoDynamo.save(new StreamEntity("paId","streamId")).block();
        StreamEntity streamEntity = streamEntityDaoDynamo.findByPa("paId").blockFirst();
        Assertions.assertEquals("paId", streamEntity.getPaId()) ;
        Assertions.assertEquals("streamId", streamEntity.getStreamId());
        Assertions.assertNotNull(streamEntity.getActivationDate());
        Assertions.assertEquals(0,streamEntity.getEventAtomicCounter());
    }

    @Test
    void get_returnsMonoOfStreamEntity() {
        streamEntityDaoDynamo.save(new StreamEntity("paId1","streamId1")).block();
        StreamEntity streamEntity = streamEntityDaoDynamo.get("paId1", "streamId1").block();
        Assertions.assertEquals("paId1", streamEntity.getPaId()) ;
        Assertions.assertEquals("streamId1", streamEntity.getStreamId());
        Assertions.assertNotNull(streamEntity.getActivationDate());
        Assertions.assertEquals(0,streamEntity.getEventAtomicCounter());
    }

    @Test
    void getWithRetryAfter_returnsTupleOfStreamEntityAndOptionalRetryAfterFound() {
        streamEntityDaoDynamo.save(new StreamEntity("paId7","streamId7")).block();
        WebhookStreamRetryAfter webhookStreamRetryAfter = new WebhookStreamRetryAfter();
        webhookStreamRetryAfter.setStreamId("streamId7");
        webhookStreamRetryAfter.setPaId("paId7");
        webhookStreamRetryAfter.setRetryAfter(Instant.now());
        streamEntityDaoDynamo.updateStreamRetryAfter(webhookStreamRetryAfter).block();

        Tuple2<StreamEntity, Optional<WebhookStreamRetryAfter>> tuple = streamEntityDaoDynamo.getWithRetryAfter("paId7", "streamId7").block();
        Assertions.assertEquals("paId7", tuple.getT1().getPaId());
        Assertions.assertEquals("streamId7", tuple.getT1().getStreamId());
        Assertions.assertEquals("paId7", tuple.getT2().get().getPaId());
        Assertions.assertEquals("RETRY#streamId7", tuple.getT2().get().getStreamId());
        Assertions.assertNotNull(tuple.getT2().get().getRetryAfter());
    }

    @Test
    void getWithRetryAfter_returnsTupleOfStreamEntityAndOptionalRetryAfterNotFound() {
        streamEntityDaoDynamo.save(new StreamEntity("paId7","streamId7")).block();
        Tuple2<StreamEntity, Optional<WebhookStreamRetryAfter>> tuple = streamEntityDaoDynamo.getWithRetryAfter("paId7", "streamId7").block();
        Assertions.assertEquals("paId7", tuple.getT1().getPaId());
        Assertions.assertEquals("streamId7", tuple.getT1().getStreamId());
        Assertions.assertFalse(tuple.getT2().isPresent());
    }

    @Test
    void delete_deletesStreamEntity() {
        streamEntityDaoDynamo.save(new StreamEntity("paId2","streamId2")).block();
        streamEntityDaoDynamo.delete("paId2", "streamId2").block();
        StreamEntity streamEntity = streamEntityDaoDynamo.get("paId2", "streamId2").block();
        Assertions.assertNull(streamEntity);
    }

    @Test
    void save_savesStreamEntity() {
        StreamEntity streamEntity = streamEntityDaoDynamo.save(new StreamEntity("paId4","streamId4")).block();
        Assertions.assertEquals("paId4", streamEntity.getPaId()) ;
        Assertions.assertEquals("streamId4", streamEntity.getStreamId());
        Assertions.assertNotNull(streamEntity.getActivationDate());
        Assertions.assertEquals(0,streamEntity.getEventAtomicCounter());
    }

    @Test
    void update_updatesStreamEntity() {
        StreamEntity streamEntity = new StreamEntity("paId5","streamId5");
        streamEntity.setTitle("title");
        StreamEntity toReplace = new StreamEntity("paId5","streamId5");
        toReplace.setTitle("changedTitle");
        streamEntityDaoDynamo.save(new StreamEntity("paId5","streamId5")).block();
        streamEntityDaoDynamo.update(toReplace).block();
        StreamEntity updatedStreamEntity = streamEntityDaoDynamo.get("paId5", "streamId5").block();
        Assertions.assertEquals("changedTitle", updatedStreamEntity.getTitle());
    }

    @Test
    void updateAndGetAtomicCounter_updatesCounterAndReturnsNewValue() {
        StreamEntity streamEntity = new StreamEntity("paId5","streamId5");
        streamEntity.setTitle("title");
        Long counter = streamEntityDaoDynamo.updateAndGetAtomicCounter(streamEntity).block();
        Assertions.assertEquals(1, counter);
    }

    @Test
    void replaceEntity_replacesOldEntityWithNewEntity() {
        StreamEntity streamEntity = new StreamEntity("paId8","streamId8");
        StreamEntity replaced = new StreamEntity("paId9","streamId9");

        streamEntityDaoDynamo.replaceEntity(streamEntity, replaced).block();
        StreamEntity updatedStreamEntity = streamEntityDaoDynamo.get("paId8", "streamId8").block();
        Assertions.assertNotNull(updatedStreamEntity.getDisabledDate());
        StreamEntity newStreamEntity = streamEntityDaoDynamo.get("paId9", "streamId9").block();
        Assertions.assertNull(newStreamEntity.getDisabledDate());

    }

    @Test
    void disable_disablesStreamEntity() {
        StreamEntity streamEntity = new StreamEntity("paId6","streamId6");
        streamEntityDaoDynamo.save(streamEntity).block();
        StreamEntity res = streamEntityDaoDynamo.disable(streamEntity).block();
        Assertions.assertNotNull(res.getDisabledDate());
    }
}
