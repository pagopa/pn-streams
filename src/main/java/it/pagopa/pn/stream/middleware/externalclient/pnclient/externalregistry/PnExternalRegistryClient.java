package it.pagopa.pn.stream.middleware.externalclient.pnclient.externalregistry;

import it.pagopa.pn.commons.log.PnLogger;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.model.SendMessageRequest;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.model.SendMessageResponse;

import java.util.List;

public interface PnExternalRegistryClient {
    String CLIENT_NAME = PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_REGISTRIES;
    String SEND_IO_MESSAGE = "SEND MESSAGE TO IO";
    
    SendMessageResponse sendIOMessage(SendMessageRequest sendMessageRequest);
    String getRootSenderId(String senderId);
    List<String> getGroups(String xPagopaPnUid, String xPagopaPnCxId );
}
