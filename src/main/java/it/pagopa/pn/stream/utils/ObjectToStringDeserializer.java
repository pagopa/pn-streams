package it.pagopa.pn.stream.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;

public class ObjectToStringDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getCurrentToken().isStructStart()) {
            ObjectNode node = p.getCodec().readTree(p);
            return node.toString();
        }
        return p.getValueAsString();
    }
}

