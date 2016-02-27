package fr.an.fxtree.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.an.fxtree.json.jackson.Jackson2FxTreeBuilder;
import fr.an.fxtree.model.FxRootDocument;

public class FxJsonUtils {

    private static ObjectMapper jacksonObjectMapper = new ObjectMapper();
    static {
        jacksonObjectMapper.enable(Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        jacksonObjectMapper.enable(Feature.ALLOW_COMMENTS);
        jacksonObjectMapper.enable(Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        // jacksonObjectMapper.enable(DeserializationFeature.);
    }
    
    public static void readTree(FxRootDocument dest, InputStream in) {
        JsonNode jacksonNode;
        try {
            jacksonNode = jacksonObjectMapper.readTree(in);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to parse as json", ex);
        }
        Jackson2FxTreeBuilder.buildTree(dest, jacksonNode);
    }

    public static void readTree(FxRootDocument dest, File in) {
        JsonNode jacksonNode;
        try {
            jacksonNode = jacksonObjectMapper.readTree(in);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to parse as json", ex);
        }
        Jackson2FxTreeBuilder.buildTree(dest, jacksonNode);
    }

}
