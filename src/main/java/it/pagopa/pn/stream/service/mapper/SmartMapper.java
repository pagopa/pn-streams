package it.pagopa.pn.stream.service.mapper;

import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import it.pagopa.pn.stream.dto.timeline.details.NormalizedAddressDetailsInt;
import it.pagopa.pn.stream.dto.timeline.details.NotificationCancelledDetailsInt;
import it.pagopa.pn.stream.dto.timeline.details.PrepareAnalogDomicileFailureDetailsInt;
import it.pagopa.pn.stream.generated.openapi.server.v1.dto.TimelineElementDetailsV26;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;


@Slf4j
public class SmartMapper {
    private static ModelMapper modelMapper;


    private static BiFunction postMappingTransformer;

    private SmartMapper (){}

    static PropertyMap<NormalizedAddressDetailsInt, TimelineElementDetailsV26> addressDetailPropertyMap = new PropertyMap<>() {
        @Override
        protected void configure() {
            skip(destination.getNewAddress());
            skip(destination.getPhysicalAddress());
        }
    };

    static PropertyMap<PrepareAnalogDomicileFailureDetailsInt, TimelineElementDetailsV26> prepareAnalogDomicileFailureDetailsInt = new PropertyMap<>() {
        @Override
        protected void configure() {
            skip(destination.getPhysicalAddress());
        }
    };

    static{
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.addMappings(addressDetailPropertyMap);
        modelMapper.addMappings(prepareAnalogDomicileFailureDetailsInt);

        modelMapper.createTypeMap(TimelineElementInternal.class, TimelineElementInternal.class);

        List<BiFunction> postMappingTransformers = new ArrayList<>();
        postMappingTransformers.add( (source, result)-> {
            if (!(source instanceof NotificationCancelledDetailsInt) && result instanceof TimelineElementDetailsV26){
                ((TimelineElementDetailsV26) result).setNotRefinedRecipientIndexes(null);
            }
            return result;
        });

        postMappingTransformer =  postMappingTransformers.stream()
                .reduce((f, g) -> (i, s) -> f.apply(i, g.apply(i, s)))
                .get();
    }


    public static  <S,T> T mapToClass(S source, Class<T> destinationClass ){
        T result;
        if( source != null) {
            result = modelMapper.map(source, destinationClass );

            result = (T) postMappingTransformer.apply(source, result);
        } else {
            result = null;
        }
        return result;
    }
}
