package it.pagopa.pn.stream.action.it.mockbean;

import it.pagopa.pn.stream.dto.ext.delivery.notification.NotificationInt;
import it.pagopa.pn.stream.middleware.externalclient.pnclient.delivery.PnDeliveryClient;
import it.pagopa.pn.stream.service.mapper.NotificationMapper;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.AnalogDomicile;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.NotificationRecipientAddressesDto;
import it.pagopa.pn.stream.generated.openapi.msclient.delivery.model.NotificationPhysicalAddress;
import it.pagopa.pn.stream.generated.openapi.msclient.delivery.model.NotificationRecipientV23;
import it.pagopa.pn.stream.generated.openapi.msclient.delivery.model.RequestUpdateStatusDto;
import it.pagopa.pn.stream.generated.openapi.msclient.delivery.model.SentNotificationV24;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Slf4j
public class PnDeliveryClientMock implements PnDeliveryClient {
    private CopyOnWriteArrayList<SentNotificationV24> notifications;

    private final PnDataVaultClientReactiveMock pnDataVaultClientReactiveMock;

    public PnDeliveryClientMock( @Lazy PnDataVaultClientReactiveMock pnDataVaultClientReactiveMock) {
        this.pnDataVaultClientReactiveMock = pnDataVaultClientReactiveMock;
    }

    public void clear() {
        this.notifications = new CopyOnWriteArrayList<>();
    }

    public void addNotification(NotificationInt notification) {
        SentNotificationV24 sentNotification = NotificationMapper.internalToExternal(notification);
        this.notifications.add(sentNotification);
        log.info("ADDED_IUN:" + notification.getIun());
    }
    
    @Override
    public void updateStatus(RequestUpdateStatusDto dto) {
        //Nothing to do
    }

    @Override
    public SentNotificationV24 getSentNotification(String iun) {
        Optional<SentNotificationV24> sentNotificationOpt = notifications.stream().filter(notification -> iun.equals(notification.getIun())).findFirst();
        if(sentNotificationOpt.isPresent()){
            SentNotificationV24 sentNotification = sentNotificationOpt.get();
            List<NotificationRecipientV23> listRecipient = sentNotification.getRecipients();
            
            int recIndex = 0;
            for (NotificationRecipientV23 recipient : listRecipient){
                
                NotificationRecipientAddressesDto recipientAddressesDto = pnDataVaultClientReactiveMock.getAddressFromRecipientIndex(iun, recIndex);
                
                if(recipientAddressesDto != null){
                    final AnalogDomicile normalizedAddress = recipientAddressesDto.getPhysicalAddress();

                    if(normalizedAddress != null){
                        NotificationPhysicalAddress physicalAddress = new NotificationPhysicalAddress()
                                .address(normalizedAddress.getAddress())
                                .addressDetails(normalizedAddress.getAddressDetails())
                                .zip(normalizedAddress.getCap())
                                .at(normalizedAddress.getAt())
                                .municipality(normalizedAddress.getMunicipality())
                                .foreignState(normalizedAddress.getState())
                                .municipalityDetails(normalizedAddress.getMunicipalityDetails())
                                .province(normalizedAddress.getProvince());

                        recipient.setPhysicalAddress(physicalAddress);
                    }
                }
                
                recIndex ++;
            }

            return sentNotificationOpt.get();
        }
        throw new RuntimeException("Test error, iun is not presente in getSentNotification IUN:" + iun);
    }

    @Override
    public Map<String, String> getQuickAccessLinkTokensPrivate(String iun) {
      Map<String, String> body = this.notifications.stream()
      .filter(n->n.getIun().equals(iun))
      .map(SentNotificationV24::getRecipients)
      .flatMap(List::stream)
      .collect(Collectors.toMap(NotificationRecipientV23::getInternalId, (n) -> "test"));
      return body;
    }
}
