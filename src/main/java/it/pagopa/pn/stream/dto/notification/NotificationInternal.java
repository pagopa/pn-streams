package it.pagopa.pn.stream.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationInternal {
    private String hashkey;
    private List<String> groups;
    private Instant creationDate;
    private Long ttl;
}