package it.pagopa.pn.stream.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.pagopa.pn.stream.dto.ConfidentialInformationEnum;
import it.pagopa.pn.stream.dto.address.PhysicalAddressInt;
import it.pagopa.pn.stream.dto.ext.datavault.ConfidentialTimelineElementDtoInt;
import it.pagopa.pn.stream.exceptions.PnStreamException;
import it.pagopa.pn.stream.service.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;

import static it.pagopa.pn.stream.exceptions.PnStreamExceptionCodes.ERROR_CODE_EVENT_CONFIDENTIAL_INFORMATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimeLineServiceImpl implements TimelineService {

    private final ObjectMapper mapper;

    @Override
    public String enrichTimelineElementWithConfidentialInformation(String details, ConfidentialTimelineElementDtoInt confidentialDto) {

        try {
            var detailsJson = mapper.readValue(details, ObjectNode.class);
            ObjectNode confidentialJson = mapper.valueToTree(confidentialDto);

            Arrays.stream(ConfidentialInformationEnum.values())
                    .forEach(confEnum -> {
                        ObjectNode targetNode = detailsJson;

                        if (StringUtils.hasText(confEnum.getParent()) && detailsJson.has(confEnum.getParent())) {
                            targetNode = (ObjectNode) detailsJson.get(confEnum.getParent());
                        }

                        if (confEnum.getType().equals(String.class)) {
                            targetNode.set(confEnum.getTimelineKey(), confidentialJson.get(confEnum.getConfidentialValue()));
                        }else if(targetNode.has(confEnum.getTimelineKey()) && confEnum.getType().equals(PhysicalAddressInt.class)){
                            targetNode.set(confEnum.getTimelineKey(), mapper.valueToTree(confidentialDto.getPhysicalAddress()));
                        }
                    });
            return detailsJson.toString();

        } catch (JsonProcessingException e) {
            throw new PnStreamException(e.getMessage(), 500, ERROR_CODE_EVENT_CONFIDENTIAL_INFORMATION);
        }
    }
}
