package it.pagopa.pn.stream.rest.it;

import it.pagopa.pn.stream.service.TimelineService;
import it.pagopa.pn.stream.service.impl.TimeLineServiceImpl;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class TestConfiguration {

    @Bean
    public TimelineService timeLineServiceImplTest() {
        return Mockito.mock(TimeLineServiceImpl.class);
    }


    
}
