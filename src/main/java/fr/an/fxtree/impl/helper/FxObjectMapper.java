package fr.an.fxtree.impl.helper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import fr.an.fxtree.format.json.jackson.FxNodeTreeTraversingParser;
import fr.an.fxtree.model.FxObjNode;

public class FxObjectMapper {

    private ObjectMapper jacksonObjectMapper = new ObjectMapper();

    // ------------------------------------------------------------------------

    public FxObjectMapper() {
    }

    // ------------------------------------------------------------------------

    public void readUpdate(FxObjNode srcObj, Object res) {
        JsonParser paramsAsJacksonParser = new FxNodeTreeTraversingParser(srcObj); 
        ObjectReader readerForUpdating = jacksonObjectMapper.readerForUpdating(res);
        try {
            readerForUpdating.readValue(paramsAsJacksonParser);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read", ex);
        }
    }
    
}
