package it.pagopa.pn.stream.config.msclient;

import it.pagopa.pn.stream.config.PnStreamConfigs;

import it.pagopa.pn.stream.generated.openapi.msclient.datavault.ApiClient;
import it.pagopa.pn.stream.generated.openapi.msclient.datavault.api.NotificationsApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DataVaultApiConfigurator {
    @Bean
    @Primary
    public NotificationsApi notificationsApi(@Qualifier("withTracing") RestTemplate restTemplate, PnStreamConfigs cfg){
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(cfg.getDataVaultBaseUrl());
        return new NotificationsApi( newApiClient );
    }
}
