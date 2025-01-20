package it.pagopa.pn.stream.middleware.externalclient.pnclient.externalregistry;

import java.util.List;

public interface PnExternalRegistryClient {
    List<String> getGroups(String xPagopaPnUid, String xPagopaPnCxId );
}
