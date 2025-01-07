package it.pagopa.pn.stream.middleware.dao.timelinedao.dynamo.entity;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LegalFactsEntity {
    private List<LegalFactsIdEntity> legalFactId;
}
