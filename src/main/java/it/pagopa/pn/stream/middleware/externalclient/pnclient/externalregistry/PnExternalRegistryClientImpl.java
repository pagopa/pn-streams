package it.pagopa.pn.stream.middleware.externalclient.pnclient.externalregistry;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.stream.exceptions.PnStreamExceptionCodes;
import it.pagopa.pn.stream.exceptions.PnRootIdNonFountException;

import java.util.List;

import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.api.InfoPaApi;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.api.RootSenderIdApi;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.api.SendIoMessageApi;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.model.PaGroup;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.model.PaGroupStatus;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.model.RootSenderIdResponse;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.model.SendMessageResponse;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.model.SendMessageRequest;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@CustomLog
@RequiredArgsConstructor
@Component
public class PnExternalRegistryClientImpl implements PnExternalRegistryClient{

    private final SendIoMessageApi sendIoMessageApi;
    private final RootSenderIdApi rootSenderIdApi;
    private final InfoPaApi infoPaApi;
    
    @Override
    public SendMessageResponse sendIOMessage(SendMessageRequest sendMessageRequest) {
        log.logInvokingExternalService(CLIENT_NAME, SEND_IO_MESSAGE);
        
        ResponseEntity<SendMessageResponse> resp;
        resp = sendIoMessageApi.sendIOMessageWithHttpInfo(sendMessageRequest);

        return resp.getBody();
    }

    @Override
    @Cacheable("aooSenderIdCache")
    public String getRootSenderId(String senderId){
        try{
            RootSenderIdResponse rootSenderIdPrivate = rootSenderIdApi.getRootSenderIdPrivate(senderId);
            return rootSenderIdPrivate.getRootId();
        }catch (Exception exc) {
            String message = String.format("Error during map rootSenderID = %s [exception received = %s]", senderId, exc);
            log.error(message);
            throw new PnRootIdNonFountException(message);
        }
    }

    @Override
    public List<String> getGroups(String xPagopaPnUid, String xPagopaPnCxId ){
        try  {
            return infoPaApi.getGroups(xPagopaPnUid, xPagopaPnCxId,null, PaGroupStatus.ACTIVE)
                .stream().map(PaGroup::getId)
                .toList();
        } catch (Exception exc) {
            String message = String.format("Error getting groups xPagopaPnUid=%s, xPagopaPnCxId=%s [exception received = %s]"
                , xPagopaPnUid,xPagopaPnCxId , exc);
            log.error(message);
            throw new PnInternalException("Error with External Registry communication", PnStreamExceptionCodes.ERROR_CODE_EXTERNAL_REGISTRY_GROUP_FAILED);
        }
    }
}