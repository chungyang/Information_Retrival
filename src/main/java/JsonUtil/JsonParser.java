package JsonUtil;

import DataObject.Corpus;
import DataObject.LookupItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class JsonParser {

    /**
     * This method parses a Json file into list of DataObject.Scene
     *
     * @param jsonFile a Json file to be parsed
     * @return list of DataObject.Scene
     */

    public Corpus parseJson2Corpus(String jsonFile){

        Corpus corpus = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            corpus = mapper.readValue(new File(jsonFile), Corpus.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return corpus;
    }

    public Map<String, LookupItem> parseJson2LookupTable(String jsonFile){

        Map<String, LookupItem> lookupTable = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            String content = new String(Files.readAllBytes(Paths.get(jsonFile)));
            lookupTable = mapper.readValue(content, new TypeReference<Map<String, LookupItem>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }


        return lookupTable;

    }
}
