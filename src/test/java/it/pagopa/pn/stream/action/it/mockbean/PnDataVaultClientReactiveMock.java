package it.pagopa.pn.stream.action.it.mockbean;

import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.BaseRecipientDto;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementDto;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.ConfidentialTimelineElementId;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.model.NotificationRecipientAddressesDto;
import it.pagopa.pn.stream.middleware.externalclient.pnclient.datavault.PnDataVaultClientReactive;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class PnDataVaultClientReactiveMock implements PnDataVaultClientReactive {
    private ConcurrentMap<String, BaseRecipientDto> confidentialMap;
    private ConcurrentMap<String, NotificationRecipientAddressesDto> normalizedAddress;
    
    public void clear() {
        this.confidentialMap = new ConcurrentHashMap<>();
        this.normalizedAddress = new ConcurrentHashMap<>();
    }
    
    public void insertBaseRecipientDto(BaseRecipientDto dto){
        confidentialMap.put(dto.getInternalId(), dto);
    }


    @Override
    public Flux<ConfidentialTimelineElementDto> getNotificationTimelines(List<ConfidentialTimelineElementId> confidentialTimelineElementId) {
        return null;
    }

    @NotNull
    private static String getKey(String iun, int recIndex) {
        return iun + "_" +recIndex;
    }

    public NotificationRecipientAddressesDto getAddressFromRecipientIndex(String iun, int rexIndex){
        String key = getKey(iun, rexIndex);
        return normalizedAddress.get(key);
    }
}