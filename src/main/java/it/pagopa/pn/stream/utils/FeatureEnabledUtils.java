package it.pagopa.pn.stream.utils;

import it.pagopa.pn.stream.config.PnStreamConfigs;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@AllArgsConstructor
@Component
public class FeatureEnabledUtils {

    private final PnStreamConfigs configs;

    public boolean isPerformanceImprovementEnabled(Instant notBefore) {
        boolean isEnabled = false;
        Instant startDate = Instant.parse(configs.getPerformanceImprovementStartDate());
        Instant endDate = Instant.parse(configs.getPerformanceImprovementEndDate());
        if ( notBefore.compareTo(startDate) >= 0 && notBefore.compareTo(endDate) <= 0) {
            isEnabled = true;
        }
        return isEnabled;
    }

}
