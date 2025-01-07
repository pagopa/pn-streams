package it.pagopa.pn.stream.service;

import it.pagopa.pn.stream.dto.ext.delivery.notification.NotificationInt;
import it.pagopa.pn.stream.dto.ext.delivery.notification.status.NotificationStatusInt;
import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

public interface StatusService {

    @Data
    @AllArgsConstructor
    class NotificationStatusUpdate{
        private NotificationStatusInt oldStatus;
        private NotificationStatusInt newStatus;
    }

    /**
     * calcola lo stato in base al dto e al set di timeline correnti
     *
     * @param dto nuova timeline
     * @param currentTimeline storico timeline attuale
     * @param notification notifica
     * @return cambio di stato elaborato
     */
     NotificationStatusUpdate computeStatusChange(TimelineElementInternal dto, Set<TimelineElementInternal> currentTimeline, NotificationInt notification);
}
