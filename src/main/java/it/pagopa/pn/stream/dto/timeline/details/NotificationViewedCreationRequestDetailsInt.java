package it.pagopa.pn.stream.dto.timeline.details;

import it.pagopa.pn.stream.dto.mandate.DelegateInfoInt;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder( toBuilder = true )
@EqualsAndHashCode
@ToString
public class NotificationViewedCreationRequestDetailsInt implements RecipientRelatedTimelineElementDetails, PersonalInformationRelatedTimelineElement{
    private int recIndex;
    private String legalFactId;
    private String raddType;
    private String raddTransactionId;
    private DelegateInfoInt delegateInfo;
    private Instant eventTimestamp;
    
    public String toLog() {
        return String.format(
                "recIndex=%d legalFactId=%s raddType=%s raddTransactionId=%s delegateInfo=%s eventTimestamp=%s" ,
                recIndex,
                legalFactId,
                raddType,
                raddTransactionId,
                delegateInfo,
                eventTimestamp
        );
    }
    
    @Override
    public String getTaxId() {
        return delegateInfo != null ? delegateInfo.getTaxId() : null;
    }

    @Override
    public void setTaxId(String taxId) {
        if(delegateInfo != null){
            delegateInfo.setTaxId(taxId);
        }
    }

    @Override
    public String getDenomination() {
        return delegateInfo != null ? delegateInfo.getDenomination() : null;
    }

    @Override
    public void setDenomination(String denomination) {
        if(delegateInfo != null){
            delegateInfo.setDenomination(denomination);
        }
    }
}