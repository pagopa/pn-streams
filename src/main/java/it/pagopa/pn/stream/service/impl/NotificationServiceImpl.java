package it.pagopa.pn.stream.service.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.stream.dto.ext.delivery.notification.NotificationInt;
import it.pagopa.pn.stream.middleware.externalclient.pnclient.delivery.PnDeliveryClient;
import it.pagopa.pn.stream.service.NotificationService;
import it.pagopa.pn.stream.service.mapper.NotificationMapper;
import it.pagopa.pn.stream.generated.openapi.msclient.delivery.model.SentNotificationV24;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static it.pagopa.pn.stream.exceptions.PnStreamExceptionCodes.ERROR_CODE_STREAM_NOTIFICATIONFAILED;


@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final PnDeliveryClient pnDeliveryClient;

    public NotificationServiceImpl(PnDeliveryClient pnDeliveryClient) {
        this.pnDeliveryClient = pnDeliveryClient;
    }

    @Override
    public NotificationInt getNotificationByIun(String iun) {
        SentNotificationV24 sentNotification = pnDeliveryClient.getSentNotification(iun);
        log.debug("Get notification OK for - iun {}", iun);

        if (sentNotification != null) {
            return NotificationMapper.externalToInternal(sentNotification);
        } else {
            log.error("Get notification is not valid for - iun {}", iun);
            throw new PnInternalException("Get notification is not valid for - iun " + iun, ERROR_CODE_STREAM_NOTIFICATIONFAILED);
        }        
    }

}
