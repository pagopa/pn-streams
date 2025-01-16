package it.pagopa.pn.stream.service.mapper;

import it.pagopa.pn.stream.dto.timeline.TimelineElementInternal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;


@Slf4j
public class SmartMapper {
    private static ModelMapper modelMapper;


    private static BiFunction postMappingTransformer;

    private SmartMapper (){}


    static{
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(TimelineElementInternal.class, TimelineElementInternal.class);

        List<BiFunction> postMappingTransformers = new ArrayList<>();
        postMappingTransformers.add( (source, result)-> result);

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
