package it.pagopa.pn.stream.middleware.externalclient.pnclient.externalregistry;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.stream.exceptions.PnStreamExceptionCodes;

import java.util.List;

import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.api.InfoPaApi;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.model.PaGroup;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.model.PaGroupStatus;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@CustomLog
@RequiredArgsConstructor
@Component
public class PnExternalRegistryClientImpl implements PnExternalRegistryClient{

    private final InfoPaApi infoPaApi;

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
