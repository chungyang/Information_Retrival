package jsonutil;


import dataobject.DocumentInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.io.StringWriter;

public class DocumentInfoSerializer extends JsonSerializer<DocumentInfo> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(DocumentInfo documentInfo, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {

        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, documentInfo);
        jsonGenerator.writeFieldName(writer.toString());

    }
}
