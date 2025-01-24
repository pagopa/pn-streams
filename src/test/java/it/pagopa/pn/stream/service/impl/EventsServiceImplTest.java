package it.pagopa.pn.stream.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.stream.config.PnStreamConfigs;
import it.pagopa.pn.stream.config.springbootcfg.AbstractCachedSsmParameterConsumerActivation;
import it.pagopa.pn.stream.dto.address.PhysicalAddressInt;
import it.pagopa.pn.stream.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.stream.dto.ext.delivery.notification.status.NotificationStatusInt;
import it.pagopa.pn.stream.dto.stream.EventTimelineInternalDto;
import it.pagopa.pn.stream.dto.stream.ProgressResponseElementDto;
import it.pagopa.pn.stream.dto.timeline.StatusInfoInternal;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.exceptions.PnStreamForbiddenException;
import it.pagopa.pn.stream.exceptions.PnTooManyRequestException;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.LegalFactCategoryV20;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.LegalFactsIdV20;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.StreamMetadataResponseV26;
import it.pagopa.pn.stream.middleware.dao.dynamo.EventEntityBatch;
import it.pagopa.pn.stream.middleware.dao.dynamo.EventEntityDao;
import it.pagopa.pn.stream.middleware.dao.dynamo.StreamEntityDao;
import it.pagopa.pn.stream.middleware.dao.dynamo.entity.EventEntity;
import it.pagopa.pn.stream.middleware.dao.dynamo.entity.StreamEntity;
import it.pagopa.pn.stream.middleware.dao.dynamo.entity.WebhookStreamRetryAfter;
import it.pagopa.pn.stream.service.ConfidentialInformationService;
import it.pagopa.pn.stream.service.SchedulerService;
import it.pagopa.pn.stream.service.TimelineService;
import it.pagopa.pn.stream.service.utils.StreamUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static it.pagopa.pn.stream.generated.openapi.server.v1.dto.TimelineElementCategoryV26.AAR_GENERATION;
import static it.pagopa.pn.stream.generated.openapi.server.v1.dto.TimelineElementCategoryV26.REQUEST_ACCEPTED;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventsServiceImplTest {
    @InjectMocks
    private StreamEventsServiceImpl webhookEventsService;
    @Mock
    private EventEntityDao eventEntityDao;
    @Mock
    private StreamEntityDao streamEntityDao;
    @Mock
    private PnStreamConfigs pnStreamConfigs;
    @Mock
    private SchedulerService schedulerService;
    @Mock
    private AbstractCachedSsmParameterConsumerActivation ssmParameterConsumerActivation;
    @Mock
    private StreamUtils webhookUtils;
    @Mock
    private TimelineService timelineService;
    @Mock
    private ConfidentialInformationService confidentialInformationService;

    Duration d = Duration.ofSeconds(3);

    @BeforeEach
    void setup() {
        when(pnStreamConfigs.getScheduleInterval()).thenReturn(1000L);
        when(pnStreamConfigs.getMaxLength()).thenReturn(10);
        when(pnStreamConfigs.getPurgeDeletionWaittime()).thenReturn(1000);
        when(pnStreamConfigs.getReadBufferDelay()).thenReturn(1000);
        when(pnStreamConfigs.getTtl()).thenReturn(Duration.ofDays(30));
        when(pnStreamConfigs.getFirstVersion()).thenReturn("v10");

        webhookEventsService = new StreamEventsServiceImpl(streamEntityDao, eventEntityDao, schedulerService,
                webhookUtils, pnStreamConfigs, timelineService, confidentialInformationService, ssmParameterConsumerActivation);
    }

    @Test
    void consumeEventStream() {
        //GIVEN
        String xpagopacxid = "PA-xpagopacxid";
        List<String> xPagopaPnCxGroups = new ArrayList<>();
        String xPagopaPnApiVersion = "v10";


        UUID uuidd = UUID.randomUUID();
        String uuid = uuidd.toString();
        StreamEntity entity = new StreamEntity();
        entity.setStreamId(uuid);
        entity.setTitle("");
        entity.setPaId(xpagopacxid);
        entity.setEventType(StreamMetadataResponseV26.EventTypeEnum.STATUS.toString());
        entity.setFilterValues(new HashSet<>());
        entity.setActivationDate(Instant.now());
        entity.setVersion("v10");


        List<EventEntity> list = new ArrayList<>();
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now() + "_" + "timeline_event_id");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);



        eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now().plusMillis(1) + "_" + "timeline_event_id2");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);

        EventEntityBatch eventEntityBatch = new EventEntityBatch();
        eventEntityBatch.setEvents(list);
        eventEntityBatch.setStreamId(uuid);
        eventEntityBatch.setLastEventIdRead(null);

        TimelineElementInternal timelineElementInternal = new TimelineElementInternal();
        timelineElementInternal.setElementId("id");
        timelineElementInternal.setTimestamp(Instant.now());
        timelineElementInternal.setIun("Iun");
        timelineElementInternal.setDetails(null);
        timelineElementInternal.setCategory(AAR_GENERATION.getValue());
        timelineElementInternal.setPaId("PaId");
        timelineElementInternal.setLegalFactsIds(new ArrayList<>());
        timelineElementInternal.setStatusInfo(null);

        ConfidentialTimelineElementDtoInt timelineElementDtoInt = new ConfidentialTimelineElementDtoInt();
        timelineElementDtoInt.toBuilder()
                .timelineElementId("id")
                .taxId("")
                .digitalAddress("")
                .physicalAddress(new PhysicalAddressInt())
                .newPhysicalAddress(new PhysicalAddressInt())
                .denomination("")
                .build();

        when(webhookUtils.getVersion("v10")).thenReturn(10);
        when(webhookUtils.getTimelineInternalFromEvent(eventEntity)).thenReturn(timelineElementInternal);
        Mockito.doNothing().when(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        when(eventEntityDao.findByStreamId(uuid, null)).thenReturn(Mono.just(eventEntityBatch));
        when(streamEntityDao.getWithRetryAfter(xpagopacxid, uuid)).thenReturn(Mono.just(Tuples.of(entity, Optional.empty())));



        //WHEN
        ProgressResponseElementDto res = webhookEventsService.consumeEventStream(xpagopacxid,xPagopaPnCxGroups,xPagopaPnApiVersion, uuidd, null).block(d);

        //THEN
        assertNotNull(res);
        Assertions.assertEquals(list.size(), res.getProgressResponseElementList().size());
        Mockito.verify(streamEntityDao).getWithRetryAfter(xpagopacxid, uuid);
        Mockito.verify(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void consumeEventStreamV10WithGroups() {
        //GIVEN
        String xpagopacxid = "PA-xpagopacxid";
        List<String> xPagopaPnCxGroups = List.of("gruppo1");
        String xPagopaPnApiVersion = "v10";


        UUID uuidd = UUID.randomUUID();
        String uuid = uuidd.toString();
        StreamEntity entity = new StreamEntity();
        entity.setStreamId(uuid);
        entity.setTitle("");
        entity.setPaId(xpagopacxid);
        entity.setEventType(StreamMetadataResponseV26.EventTypeEnum.STATUS.toString());
        entity.setFilterValues(new HashSet<>());
        entity.setActivationDate(Instant.now());
        entity.setVersion("v10");


        List<EventEntity> list = new ArrayList<>();
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now() + "_" + "timeline_event_id");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);



        eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now().plusMillis(1) + "_" + "timeline_event_id2");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);

        EventEntityBatch eventEntityBatch = new EventEntityBatch();
        eventEntityBatch.setEvents(list);
        eventEntityBatch.setStreamId(uuid);
        eventEntityBatch.setLastEventIdRead(null);

        TimelineElementInternal timelineElementInternal = new TimelineElementInternal();
        timelineElementInternal.setElementId("id");
        timelineElementInternal.setTimestamp(Instant.now());
        timelineElementInternal.setIun("Iun");
        timelineElementInternal.setDetails(null);
        timelineElementInternal.setCategory(AAR_GENERATION.getValue());
        timelineElementInternal.setPaId("PaId");
        timelineElementInternal.setLegalFactsIds(new ArrayList<>());
        timelineElementInternal.setStatusInfo(null);

        ConfidentialTimelineElementDtoInt timelineElementDtoInt = new ConfidentialTimelineElementDtoInt();
        timelineElementDtoInt.toBuilder()
            .timelineElementId("id")
            .taxId("")
            .digitalAddress("")
            .physicalAddress(new PhysicalAddressInt())
            .newPhysicalAddress(new PhysicalAddressInt())
            .denomination("")
            .build();

        when(streamEntityDao.getWithRetryAfter(xpagopacxid, uuid)).thenReturn(Mono.just(Tuples.of(entity, Optional.empty())));
        when(webhookUtils.getVersion("v10")).thenReturn(10);
        when(webhookUtils.getTimelineInternalFromEvent(eventEntity)).thenReturn(timelineElementInternal);
        Mockito.doNothing().when(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        when(eventEntityDao.findByStreamId(uuid, null)).thenReturn(Mono.just(eventEntityBatch));



        //WHEN
        ProgressResponseElementDto res = webhookEventsService.consumeEventStream(xpagopacxid,xPagopaPnCxGroups,xPagopaPnApiVersion, uuidd, null).block(d);

        //THEN
        assertNotNull(res);
        Assertions.assertEquals(list.size(), res.getProgressResponseElementList().size());
        Mockito.verify(streamEntityDao).getWithRetryAfter(xpagopacxid, uuid);
        Mockito.verify(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void consumeEventStream2Forbidden() {
        //GIVEN
        String xpagopacxid = "PA-xpagopacxid";
        String xPagopaPnApiVersion = "v23";

        UUID uuidd = UUID.randomUUID();
        String uuid = uuidd.toString();
        StreamEntity entity = new StreamEntity();
        entity.setStreamId(uuid);
        entity.setTitle("1");
        entity.setPaId(xpagopacxid);
        entity.setEventType(StreamMetadataResponseV26.EventTypeEnum.STATUS.toString());
        entity.setFilterValues(new HashSet<>());
        entity.setActivationDate(Instant.now());
        entity.setVersion("v23");
        entity.setGroups(Collections.emptyList());


        when(streamEntityDao.getWithRetryAfter(xpagopacxid, uuid)).thenReturn(Mono.just(Tuples.of(entity, Optional.empty())));
        Mockito.doNothing().when(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        when(eventEntityDao.findByStreamId(uuid, null)).thenReturn(Mono.empty());


        //WHEN
        Mono<ProgressResponseElementDto> mono = webhookEventsService.consumeEventStream(xpagopacxid, List.of("gruppo1"), xPagopaPnApiVersion, uuidd, null);
        assertThrows(PnStreamForbiddenException.class, () -> mono.block(d));

        //THEN
        Mockito.verify(eventEntityDao, Mockito.never()).findByStreamId(Mockito.anyString(), Mockito.any());
        Mockito.verify(schedulerService, Mockito.never()).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());

    }


    @Test
    void consumeEventStreamNearEvents() {
        //GIVEN
        String xpagopacxid = "PA-xpagopacxid";
        String lasteventid;
        List<String> xPagopaPnCxGroups = new ArrayList<>();
        String xPagopaPnApiVersion = "v10";


        UUID uuidd = UUID.randomUUID();
        String uuid = uuidd.toString();
        StreamEntity entity = new StreamEntity();
        entity.setStreamId(uuid);
        entity.setTitle("1");
        entity.setPaId(xpagopacxid);
        entity.setEventType(StreamMetadataResponseV26.EventTypeEnum.STATUS.toString());
        entity.setFilterValues(new HashSet<>());
        entity.setActivationDate(Instant.now());


        List<EventEntity> list = new ArrayList<>();
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now() + "_" + "timeline_event_id");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);



        eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now().plusMillis(1) + "_" + "timeline_event_id2");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);

        EventEntityBatch eventEntityBatch = new EventEntityBatch();
        eventEntityBatch.setEvents(list);
        eventEntityBatch.setStreamId(uuid);
        eventEntityBatch.setLastEventIdRead(null);

        TimelineElementInternal timelineElementInternal = new TimelineElementInternal();


        lasteventid = list.get(0).getEventId();

        when(streamEntityDao.getWithRetryAfter(xpagopacxid, uuid)).thenReturn(Mono.just(Tuples.of(entity, Optional.empty())));
        Mockito.doNothing().when(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        when(webhookUtils.getTimelineInternalFromEvent(Mockito.any())).thenReturn(timelineElementInternal);
        when(eventEntityDao.findByStreamId(Mockito.anyString() , Mockito.anyString())).thenReturn(Mono.just(eventEntityBatch));
        when(webhookUtils.getVersion(xPagopaPnApiVersion)).thenReturn(10);


        //WHEN
        ProgressResponseElementDto res = webhookEventsService.consumeEventStream(xpagopacxid,xPagopaPnCxGroups,xPagopaPnApiVersion, uuidd, lasteventid).block(d);

        //THEN
        assertNotNull(res);
        Assertions.assertEquals(2, res.getProgressResponseElementList().size());
        Mockito.verify(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
    }


    @Test
    void consumeEventStreamNoEvents() {
        //GIVEN
        String xpagopacxid = "PA-xpagopacxid";
        String lasteventid;
        List<String> xPagopaPnCxGroups = new ArrayList<>();
        String xPagopaPnApiVersion = "v10";


        UUID uuidd = UUID.randomUUID();
        String uuid = uuidd.toString();
        StreamEntity entity = new StreamEntity();
        entity.setStreamId(uuid);
        entity.setTitle("1");
        entity.setPaId(xpagopacxid);
        entity.setEventType(StreamMetadataResponseV26.EventTypeEnum.STATUS.toString());
        entity.setFilterValues(new HashSet<>());
        entity.setActivationDate(Instant.now());


        List<EventEntity> list = new ArrayList<>();
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now() + "_" + "timeline_event_id");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);



        eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now().plusMillis(1) + "_" + "timeline_event_id2");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);

        EventEntityBatch eventEntityBatch = new EventEntityBatch();
        eventEntityBatch.setEvents(Collections.emptyList());
        eventEntityBatch.setStreamId(uuid);
        eventEntityBatch.setLastEventIdRead(null);

        TimelineElementInternal timelineElementInternal = new TimelineElementInternal();


        lasteventid = list.get(0).getEventId();

        when(streamEntityDao.getWithRetryAfter(xpagopacxid, uuid)).thenReturn(Mono.just(Tuples.of(entity, Optional.empty())));
        Mockito.doNothing().when(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        when(webhookUtils.getTimelineInternalFromEvent(Mockito.any())).thenReturn(timelineElementInternal);
        when(eventEntityDao.findByStreamId(Mockito.anyString() , Mockito.anyString())).thenReturn(Mono.just(eventEntityBatch));
        when(webhookUtils.getVersion(xPagopaPnApiVersion)).thenReturn(10);
        when(ssmParameterConsumerActivation.getParameterValue(any(), any())).thenReturn(Optional.empty());
        when(streamEntityDao.updateStreamRetryAfter(any())).thenReturn(Mono.empty());


        //WHEN
        ProgressResponseElementDto res = webhookEventsService.consumeEventStream(xpagopacxid,xPagopaPnCxGroups,xPagopaPnApiVersion, uuidd, lasteventid).block(d);

        //THEN
        assertNotNull(res);
        Assertions.assertEquals(0, res.getProgressResponseElementList().size());
        Assertions.assertEquals(1000, res.getRetryAfter());
        Mockito.verify(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void consumeEventStreamRetryAfterViolationNoException() {
        //GIVEN
        String xpagopacxid = "PA-xpagopacxid";
        String lasteventid;
        List<String> xPagopaPnCxGroups = new ArrayList<>();
        String xPagopaPnApiVersion = "v10";


        UUID uuidd = UUID.randomUUID();
        String uuid = uuidd.toString();
        StreamEntity entity = new StreamEntity();
        entity.setStreamId(uuid);
        entity.setTitle("1");
        entity.setPaId(xpagopacxid);
        entity.setEventType(StreamMetadataResponseV26.EventTypeEnum.STATUS.toString());
        entity.setFilterValues(new HashSet<>());
        entity.setActivationDate(Instant.now());


        List<EventEntity> list = new ArrayList<>();
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now() + "_" + "timeline_event_id");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);



        eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now().plusMillis(1) + "_" + "timeline_event_id2");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);

        EventEntityBatch eventEntityBatch = new EventEntityBatch();
        eventEntityBatch.setEvents(list);
        eventEntityBatch.setStreamId(uuid);
        eventEntityBatch.setLastEventIdRead(null);

        TimelineElementInternal timelineElementInternal = new TimelineElementInternal();

        WebhookStreamRetryAfter webhookStreamRetryAfter = new WebhookStreamRetryAfter();
        webhookStreamRetryAfter.setPaId(xpagopacxid);
        webhookStreamRetryAfter.setStreamId(uuid);
        webhookStreamRetryAfter.setRetryAfter(Instant.now().plusMillis(10000));


        lasteventid = list.get(0).getEventId();

        when(streamEntityDao.getWithRetryAfter(xpagopacxid, uuid)).thenReturn(Mono.just(Tuples.of(entity, Optional.of(webhookStreamRetryAfter))));
        Mockito.doNothing().when(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        when(webhookUtils.getTimelineInternalFromEvent(Mockito.any())).thenReturn(timelineElementInternal);
        when(eventEntityDao.findByStreamId(Mockito.anyString() , Mockito.anyString())).thenReturn(Mono.just(eventEntityBatch));
        when(webhookUtils.getVersion(xPagopaPnApiVersion)).thenReturn(10);

        //WHEN
        ProgressResponseElementDto res = webhookEventsService.consumeEventStream(xpagopacxid,xPagopaPnCxGroups,xPagopaPnApiVersion, uuidd, lasteventid).block(d);

        //THEN
        assertNotNull(res);
        Assertions.assertEquals(2, res.getProgressResponseElementList().size());
        Mockito.verify(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void consumeEventStreamRetryAfterViolationException() {
        when(pnStreamConfigs.getRetryAfterEnabled()).thenReturn(Boolean.TRUE);
        //GIVEN
        String xpagopacxid = "PA-xpagopacxid";
        String lasteventid;
        List<String> xPagopaPnCxGroups = new ArrayList<>();
        String xPagopaPnApiVersion = "v10";


        UUID uuidd = UUID.randomUUID();
        String uuid = uuidd.toString();
        StreamEntity entity = new StreamEntity();
        entity.setStreamId(uuid);
        entity.setTitle("1");
        entity.setPaId(xpagopacxid);
        entity.setEventType(StreamMetadataResponseV26.EventTypeEnum.STATUS.toString());
        entity.setFilterValues(new HashSet<>());
        entity.setActivationDate(Instant.now());


        List<EventEntity> list = new ArrayList<>();
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now() + "_" + "timeline_event_id");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);



        eventEntity = new EventEntity();
        eventEntity.setEventId(Instant.now().plusMillis(1) + "_" + "timeline_event_id2");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setTimelineEventCategory(AAR_GENERATION.getValue());
        eventEntity.setNewStatus(NotificationStatusInt.ACCEPTED.getValue());
        eventEntity.setIun("");
        eventEntity.setNotificationRequestId("");
        eventEntity.setStreamId(uuid);
        list.add(eventEntity);

        EventEntityBatch eventEntityBatch = new EventEntityBatch();
        eventEntityBatch.setEvents(list);
        eventEntityBatch.setStreamId(uuid);
        eventEntityBatch.setLastEventIdRead(null);

        TimelineElementInternal timelineElementInternal = new TimelineElementInternal();

        WebhookStreamRetryAfter webhookStreamRetryAfter = new WebhookStreamRetryAfter();
        webhookStreamRetryAfter.setPaId(xpagopacxid);
        webhookStreamRetryAfter.setStreamId(uuid);
        webhookStreamRetryAfter.setRetryAfter(Instant.now().plusMillis(10000));


        lasteventid = list.get(0).getEventId();

        when(streamEntityDao.getWithRetryAfter(xpagopacxid, uuid)).thenReturn(Mono.just(Tuples.of(entity, Optional.of(webhookStreamRetryAfter))));
        Mockito.doNothing().when(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        when(webhookUtils.getTimelineInternalFromEvent(Mockito.any())).thenReturn(timelineElementInternal);
        when(eventEntityDao.findByStreamId(Mockito.anyString() , Mockito.anyString())).thenReturn(Mono.just(eventEntityBatch));
        when(webhookUtils.getVersion(xPagopaPnApiVersion)).thenReturn(10);


        //WHEN
        Assertions.assertThrows(PnTooManyRequestException.class, () -> webhookEventsService.consumeEventStream(xpagopacxid,xPagopaPnCxGroups,xPagopaPnApiVersion, uuidd, lasteventid).block(d));

    }

    @Test
    void consumeEventStreamForbidden() {
        //GIVEN
        String xpagopacxid = "PA-xpagopacxid";
        String xPagopaPnApiVersion = "v23";

        UUID uuidd = UUID.randomUUID();
        String uuid = uuidd.toString();
        StreamEntity entity = new StreamEntity();
        entity.setStreamId(uuid);
        entity.setTitle("1");
        entity.setPaId(xpagopacxid);
        entity.setEventType(StreamMetadataResponseV26.EventTypeEnum.STATUS.toString());
        entity.setFilterValues(new HashSet<>());
        entity.setActivationDate(Instant.now());
        entity.setVersion("v10");


        when(streamEntityDao.getWithRetryAfter(xpagopacxid, uuid)).thenReturn(Mono.just(Tuples.of(entity, Optional.empty())));
        Mockito.doNothing().when(schedulerService).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        when(eventEntityDao.findByStreamId(uuid, null)).thenReturn(Mono.empty());


        //WHEN
        Mono<ProgressResponseElementDto> mono = webhookEventsService.consumeEventStream(xpagopacxid, null, xPagopaPnApiVersion, uuidd, null);
        assertThrows(PnStreamForbiddenException.class, () -> mono.block(d));

        //THEN
        Mockito.verify(eventEntityDao, Mockito.never()).findByStreamId(Mockito.anyString(), Mockito.any());
        Mockito.verify(schedulerService, Mockito.never()).scheduleStreamEvent(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    void addConfidentialInformationAtEventTimelineList() {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventId("eventId");
        eventEntity.setIun("iun");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setTimelineEventCategory(REQUEST_ACCEPTED.getValue());
        eventEntity.setEventDescription("eventDescription");
        eventEntity.setNewStatus("newStatus");
        eventEntity.setStreamId("streamId");
        eventEntity.setChannel("channel");
        eventEntity.setElement("element");
        eventEntity.setNotificationRequestId("notificationRequestId");
        TimelineElementInternal timelineElementInternal = TimelineElementInternal.builder()
                .elementId("elementId")
                .category(REQUEST_ACCEPTED.getValue())
                .timestamp(Instant.now())
                .paId("paId")
                .statusInfo(StatusInfoInternal.builder().actual("actual").statusChanged(true).build())
                .legalFactsIds(List.of(LegalFactsIdV20.builder().key("key").category(LegalFactCategoryV20.DIGITAL_DELIVERY).build()))
                .build();

        EventTimelineInternalDto eventTimelineInternalDto = EventTimelineInternalDto.builder()
                .eventEntity(eventEntity)
                .timelineElementInternal(timelineElementInternal)
                .build();

        ConfidentialTimelineElementDtoInt confidentialTimelineElementDtoInt = ConfidentialTimelineElementDtoInt.builder()
                .timelineElementId("elementId")
                .taxId("taxId")
                .denomination("denomination")
                .digitalAddress("digitalAddress")
                .physicalAddress(PhysicalAddressInt.builder().address("via address").build())
                .build();

        Flux<ConfidentialTimelineElementDtoInt> flux = Flux.just(confidentialTimelineElementDtoInt);
        when(confidentialInformationService.getTimelineConfidentialInformation(List.of(timelineElementInternal)))
                .thenReturn(flux);

        Flux<EventTimelineInternalDto> fluxDto = webhookEventsService.addConfidentialInformationAtEventTimelineList(List.of(eventTimelineInternalDto));

        Assertions.assertNotNull(fluxDto);

        EventTimelineInternalDto dto = fluxDto.blockFirst();

        assert dto != null;
        Assertions.assertEquals("eventId", dto.getEventEntity().getEventId());
        Assertions.assertEquals("iun", dto.getEventEntity().getIun());
        Assertions.assertEquals("element", dto.getEventEntity().getElement());
        Assertions.assertEquals("newStatus", dto.getEventEntity().getNewStatus());
        Assertions.assertEquals(REQUEST_ACCEPTED.getValue(), dto.getEventEntity().getTimelineEventCategory());
        Assertions.assertEquals("streamId", dto.getEventEntity().getStreamId());
        Assertions.assertEquals("channel", dto.getEventEntity().getChannel());
        Assertions.assertEquals("notificationRequestId", dto.getEventEntity().getNotificationRequestId());

        Assertions.assertEquals("elementId", dto.getTimelineElementInternal().getElementId());
        Assertions.assertEquals(REQUEST_ACCEPTED.getValue(), dto.getTimelineElementInternal().getCategory());
        Assertions.assertEquals("paId", dto.getTimelineElementInternal().getPaId());
        Assertions.assertEquals("actual", dto.getTimelineElementInternal().getStatusInfo().getActual());
    }

    @Test
    void addConfidentialInformationAtEventTimelineListKo() {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventId("eventId");
        eventEntity.setIun("iun");
        eventEntity.setTimestamp(Instant.now());
        eventEntity.setEventDescription("eventDescription");
        eventEntity.setNewStatus("newStatus");
        eventEntity.setStreamId("streamId");
        eventEntity.setChannel("channel");
        eventEntity.setElement("element");
        eventEntity.setNotificationRequestId("notificationRequestId");

        TimelineElementInternal timelineElementInternal = TimelineElementInternal.builder()
                .elementId("elementId")
                .category(REQUEST_ACCEPTED.getValue())
                .timestamp(Instant.now())
                .paId("paId")
                .statusInfo(StatusInfoInternal.builder().actual("actual").statusChanged(true).build())
                .legalFactsIds(List.of(LegalFactsIdV20.builder().key("key").category(LegalFactCategoryV20.DIGITAL_DELIVERY).build()))
                .build();

        EventTimelineInternalDto eventTimelineInternalDto = EventTimelineInternalDto.builder()
                .eventEntity(eventEntity)
                .timelineElementInternal(timelineElementInternal)
                .build();

        when(confidentialInformationService.getTimelineConfidentialInformation(anyList())).thenReturn(Flux.error(new PnInternalException("error", 500, "error")));

        List<EventTimelineInternalDto> list = List.of(eventTimelineInternalDto);
        var resp = webhookEventsService.addConfidentialInformationAtEventTimelineList(list);

        Assertions.assertThrows(PnInternalException.class, resp::blockFirst);
    }
}