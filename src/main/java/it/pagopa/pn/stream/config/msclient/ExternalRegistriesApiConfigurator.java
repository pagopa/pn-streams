package it.pagopa.pn.stream.config.msclient;

import it.pagopa.pn.stream.config.PnStreamConfigs;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.ApiClient;
import it.pagopa.pn.stream.generated.openapi.msclient.externalregistry.api.InfoPaApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ExternalRegistriesApiConfigurator {
    @Bean
    public InfoPaApi infoPaApi(@Qualifier("withTracing") RestTemplate restTemplate, PnStreamConfigs cfg) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath( cfg.getExternalRegistryBaseUrl() );
        return new InfoPaApi(newApiClient);
    }
}
