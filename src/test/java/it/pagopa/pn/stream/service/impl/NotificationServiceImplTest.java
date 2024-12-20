package it.pagopa.pn.stream.service.impl;

import it.pagopa.pn.commons.exceptions.PnHttpResponseException;
import it.pagopa.pn.stream.dto.ext.delivery.notification.NotificationInt;
import it.pagopa.pn.stream.dto.ext.delivery.notification.NotificationSenderInt;
import it.pagopa.pn.stream.dto.ext.delivery.notification.ServiceLevelTypeInt;
import it.pagopa.pn.stream.middleware.externalclient.pnclient.delivery.PnDeliveryClient;
import it.pagopa.pn.stream.generated.openapi.msclient.delivery.model.NotificationFeePolicy;
import it.pagopa.pn.stream.generated.openapi.msclient.delivery.model.SentNotificationV24;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

class NotificationServiceImplTest {

    @Mock
    private PnDeliveryClient pnDeliveryClient;

    private NotificationServiceImpl service;

    @BeforeEach
    public void setup() {
        service = new NotificationServiceImpl(pnDeliveryClient);
    }

    @Test
    @ExtendWith(SpringExtension.class)
    void getNotificationByIun() {
        NotificationInt expected = buildNotificationInt();

        SentNotificationV24 sentNotification = buildSentNotification();
        Mockito.when(pnDeliveryClient.getSentNotification("001")).thenReturn(sentNotification);

        NotificationInt actual = service.getNotificationByIun("001");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @ExtendWith(SpringExtension.class)
    void getNotificationByIunNotFound() {

        Mockito.when(pnDeliveryClient.getSentNotification("001")).thenThrow(PnHttpResponseException.class);

        Assertions.assertThrows(PnHttpResponseException.class, () -> service.getNotificationByIun("001"));

    }

    
    private SentNotificationV24 buildSentNotification() {
        SentNotificationV24 sentNotification = new SentNotificationV24();
        sentNotification.setIun("001");
        sentNotification.setPhysicalCommunicationType(SentNotificationV24.PhysicalCommunicationTypeEnum.REGISTERED_LETTER_890);
        sentNotification.setNotificationFeePolicy(NotificationFeePolicy.DELIVERY_MODE);
        return sentNotification;
    }

    private NotificationInt buildNotificationInt() {
        return NotificationInt.builder()
                .iun("001")
                .recipients(Collections.emptyList())
                .documents(Collections.emptyList())
                .sender(NotificationSenderInt.builder().build())
                .notificationFeePolicy(NotificationFeePolicy.DELIVERY_MODE)
                .physicalCommunicationType(ServiceLevelTypeInt.REGISTERED_LETTER_890)
                .build();
    }
}