package jsonutil;


import dataobject.LookupItem;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.io.StringWriter;

public class LookupItemSerializer extends JsonSerializer<LookupItem>{

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(LookupItem lookupItem, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {

        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, lookupItem);
        jsonGenerator.writeFieldName(writer.toString());

    }
}
