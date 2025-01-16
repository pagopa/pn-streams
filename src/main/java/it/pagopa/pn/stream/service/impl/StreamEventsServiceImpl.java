package it.pagopa.pn.stream.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.log.PnAuditLogEventType;
import it.pagopa.pn.stream.config.PnStreamConfigs;
import it.pagopa.pn.stream.config.springbootcfg.SsmParameterConsumerActivation;
import it.pagopa.pn.stream.dto.stream.CustomRetryAfterParameter;
import it.pagopa.pn.stream.dto.stream.EventTimelineInternalDto;
import it.pagopa.pn.stream.dto.stream.ProgressResponseElementDto;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.exceptions.PnStreamForbiddenException;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.ProgressResponseElementV26;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.TimelineElementV26;
import it.pagopa.pn.stream.middleware.dao.dynamo.EventEntityDao;
import it.pagopa.pn.stream.middleware.dao.dynamo.StreamEntityDao;
import it.pagopa.pn.stream.middleware.dao.dynamo.entity.EventEntity;
import it.pagopa.pn.stream.middleware.dao.dynamo.entity.WebhookStreamRetryAfter;
import it.pagopa.pn.stream.middleware.queue.producer.abstractions.streamspool.StreamEventType;
import it.pagopa.pn.stream.service.ConfidentialInformationService;
import it.pagopa.pn.stream.service.SchedulerService;
import it.pagopa.pn.stream.service.StreamEventsService;
import it.pagopa.pn.stream.service.TimelineService;
import it.pagopa.pn.stream.service.mapper.ProgressResponseElementMapper;
import it.pagopa.pn.stream.service.mapper.TimelineElementStreamMapper;
import it.pagopa.pn.stream.service.utils.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_GENERIC_ERROR;


@Service
@Slf4j
public class StreamEventsServiceImpl extends PnStreamServiceImpl implements StreamEventsService {
    private final EventEntityDao eventEntityDao;
    private final SchedulerService schedulerService;
    private final StreamUtils streamUtils;
    private final TimelineService timelineService;
    private final ConfidentialInformationService confidentialInformationService;

    private final SsmParameterConsumerActivation ssmParameterConsumerActivation;


    public StreamEventsServiceImpl(StreamEntityDao streamEntityDao, EventEntityDao eventEntityDao,
                                   SchedulerService schedulerService, StreamUtils streamUtils,
                                   PnStreamConfigs pnStreamConfigs, TimelineService timeLineService,
                                   ConfidentialInformationService confidentialInformationService,
                                   SsmParameterConsumerActivation ssmParameterConsumerActivation) {
        super(streamEntityDao, pnStreamConfigs);
        this.eventEntityDao = eventEntityDao;
        this.schedulerService = schedulerService;
        this.streamUtils = streamUtils;
        this.timelineService = timeLineService;
        this.confidentialInformationService = confidentialInformationService;
        this.ssmParameterConsumerActivation = ssmParameterConsumerActivation;
    }

    @Override
    public Mono<ProgressResponseElementDto> consumeEventStream(String xPagopaPnCxId,
        List<String> xPagopaPnCxGroups,
        String xPagopaPnApiVersion,
        UUID streamId,
        String lastEventId) {
        String msg = "consumeEventStream xPagopaPnCxId={}, xPagopaPnCxGroups={}, xPagopaPnApiVersion={}, streamId={} ";
        String[] args = {xPagopaPnCxId, groupString(xPagopaPnCxGroups), xPagopaPnApiVersion, streamId.toString()};
        generateAuditLog(PnAuditLogEventType.AUD_WH_CONSUME, msg, args).log();
        // grazie al contatore atomico usato in scrittura per generare l'eventId, non serve più gestire la finestra.
        return getStreamEntityToWrite(apiVersion(xPagopaPnApiVersion), xPagopaPnCxId, xPagopaPnCxGroups, streamId, true)
            .doOnError(error -> generateAuditLog(PnAuditLogEventType.AUD_WH_CONSUME, msg, args).generateFailure("Error in reading stream").log())
            .switchIfEmpty(Mono.error(new PnStreamForbiddenException("Cannot consume stream")))
            .flatMap(stream -> eventEntityDao.findByStreamId(stream.getStreamId(), lastEventId))
                .flatMap(res ->
                    toEventTimelineInternalFromEventEntity(res.getEvents())
                            .onErrorResume(ex -> Mono.error(new PnInternalException("Timeline element entity not converted into JSON", ERROR_CODE_PN_GENERIC_ERROR)))
                            //timeline ancora anonimizzato - EventEntity + TimelineElementInternal
                            .collectList()
                            // chiamo timelineService per aggiungere le confidentialInfo
                            .flatMapMany(items -> {
                                if (streamUtils.getVersion(xPagopaPnApiVersion) == 10)
                                    return Flux.fromStream(items.stream());
                                return addConfidentialInformationAtEventTimelineList(removeDuplicatedItems(items));
                            })
                            // converto l'eventTimelineInternalDTO in ProgressResponseElementV26
                            .map(this::getProgressResponseFromEventTimeline)
                            .sort(Comparator.comparing(ProgressResponseElementV26::getEventId))
                            .collectList()
                            .flatMap(eventList -> {
                                if (eventList.isEmpty()) {
                                    return streamEntityDao.updateStreamRetryAfter(constructNewRetryAfterEntity(xPagopaPnCxId, streamId))
                                            .thenReturn(eventList);
                                }
                                return Mono.just(eventList);
                            })
                            .map(eventList -> {
                                var retryAfter = pnStreamConfigs.getScheduleInterval().intValue();
                                int currentRetryAfter = res.getLastEventIdRead() == null ? retryAfter : 0;
                                var purgeDeletionWaittime = pnStreamConfigs.getPurgeDeletionWaittime();
                                log.info("consumeEventStream lastEventId={} streamId={} size={} returnedlastEventId={} retryAfter={}", lastEventId, streamId, eventList.size(), (!eventList.isEmpty()?eventList.get(eventList.size()-1).getEventId():"ND"), currentRetryAfter);
                                // schedulo la pulizia per gli eventi precedenti a quello richiesto
                                schedulerService.scheduleStreamEvent(res.getStreamId(), lastEventId, purgeDeletionWaittime, StreamEventType.PURGE_STREAM_OLDER_THAN);
                                // ritorno gli eventi successivi all'evento di buffer, FILTRANDO quello con lastEventId visto che l'ho sicuramente già ritornato
                                return ProgressResponseElementDto.builder()
                                        .retryAfter(currentRetryAfter)
                                        .progressResponseElementList(eventList)
                                        .build();
                            })
                            .doOnSuccess(progressResponseElementDto -> generateAuditLog(PnAuditLogEventType.AUD_WH_CONSUME, msg, args).generateSuccess("ProgressResponseElementDto size={}", progressResponseElementDto.getProgressResponseElementList().size()).log())
                            .doOnError(error -> generateAuditLog(PnAuditLogEventType.AUD_WH_CONSUME, msg, args).generateFailure("Error in consumeEventStream").log())
                );
    }

    private WebhookStreamRetryAfter constructNewRetryAfterEntity(String xPagopaPnCxId, UUID streamId) {
        WebhookStreamRetryAfter retryAfterEntity = new WebhookStreamRetryAfter();
        retryAfterEntity.setPaId(xPagopaPnCxId);
        retryAfterEntity.setStreamId(streamId.toString());
        retryAfterEntity.setRetryAfter(retrieveRetryAfter(xPagopaPnCxId));
        return retryAfterEntity;
    }

    private Instant retrieveRetryAfter(String xPagopaPnCxId) {
        return ssmParameterConsumerActivation.getParameterValue(pnStreamConfigs.getRetryParameterPrefix() + xPagopaPnCxId, CustomRetryAfterParameter.class)
                .map(customRetryAfterParameter -> Instant.now().plusMillis(customRetryAfterParameter.getRetryAfter()))
                .orElse(Instant.now().plusMillis(pnStreamConfigs.getScheduleInterval()));
    }

    private ProgressResponseElementV26 getProgressResponseFromEventTimeline(EventTimelineInternalDto eventTimeline) {
        var response = ProgressResponseElementMapper.internalToExternal(eventTimeline.getEventEntity());
        if (StringUtils.hasText(eventTimeline.getEventEntity().getElement())) {
            TimelineElementV26 timelineElement = TimelineElementStreamMapper.internalToExternal(eventTimeline.getTimelineElementInternal());
            response.setElement(timelineElement);
        }
        return response;
    }

    private Flux<EventTimelineInternalDto> toEventTimelineInternalFromEventEntity(List<EventEntity> events) throws PnInternalException{
        return Flux.fromStream(events.stream())
                .map(item -> {
                    TimelineElementInternal timelineElementInternal = getTimelineInternalFromEventEntity(item);
                    return EventTimelineInternalDto.builder()
                            .eventEntity(item)
                            .timelineElementInternal(timelineElementInternal)
                            .build();
                });
    }

    private TimelineElementInternal getTimelineInternalFromEventEntity(EventEntity entity) throws PnInternalException{
        if (StringUtils.hasText(entity.getElement())) {
            return streamUtils.getTimelineInternalFromEvent(entity);
        }
        return null;
    }
    private List<EventTimelineInternalDto> removeDuplicatedItems(List<EventTimelineInternalDto> eventEntities) {
        return new ArrayList<>(eventEntities.stream()
                .collect(Collectors.toMap(
                        dto -> dto.getTimelineElementInternal().getElementId(),
                        dto -> dto,
                        (existing, replacement) -> existing
                )).values());
    }

    protected Flux<EventTimelineInternalDto> addConfidentialInformationAtEventTimelineList(List<EventTimelineInternalDto> eventEntities) {
        List<TimelineElementInternal> timelineElementInternals = eventEntities.stream()
                .map(EventTimelineInternalDto::getTimelineElementInternal)
                .filter(Objects::nonNull)
                .toList();

        return confidentialInformationService.getTimelineConfidentialInformation(timelineElementInternals)
                .map(confidentialInfo -> {
                    // cerco l'elemento in TimelineElementInternals con elementiId
                    TimelineElementInternal internal = timelineElementInternals.stream()
                            .filter(i -> i.getElementId().equals(confidentialInfo.getTimelineElementId()))
                            .findFirst()
                            .get();
                   //TODO: FIX timelineService.enrichTimelineElementWithConfidentialInformation(internal.getDetails(), confidentialInfo);
                    return internal;
                })
                .collectList()
                .flatMapMany(item -> Flux.fromStream(eventEntities.stream()));
    }
}