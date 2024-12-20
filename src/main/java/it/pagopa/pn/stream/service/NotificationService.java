package it.pagopa.pn.stream.service;

import it.pagopa.pn.stream.dto.ext.delivery.notification.NotificationInt;

public interface NotificationService {
    NotificationInt getNotificationByIun(String iun);

}
