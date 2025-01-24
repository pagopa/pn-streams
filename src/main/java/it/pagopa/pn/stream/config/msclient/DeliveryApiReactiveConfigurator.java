package it.pagopa.pn.stream.config.msclient;

import it.pagopa.pn.commons.pnclients.CommonBaseClient;
import it.pagopa.pn.deliverypush.generated.openapi.msclient.delivery_reactive.ApiClient;
import it.pagopa.pn.deliverypush.generated.openapi.msclient.delivery_reactive.api.InternalOnlyApi;
import it.pagopa.pn.stream.config.PnStreamConfigs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeliveryApiReactiveConfigurator extends CommonBaseClient {

    @Bean
    public InternalOnlyApi internalOnlyApiReactive(PnStreamConfigs cfg){
        ApiClient apiClient = new ApiClient(initWebClient(ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(cfg.getDeliveryBaseUrl());
        return new InternalOnlyApi(apiClient);
    }
}
