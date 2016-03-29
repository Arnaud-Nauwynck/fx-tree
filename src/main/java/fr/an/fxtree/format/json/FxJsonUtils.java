package fr.an.fxtree.format.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.an.fxtree.format.json.jackson.Jackson2FxTreeBuilder;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

public class FxJsonUtils {

    private static ObjectMapper jacksonObjectMapper = new ObjectMapper();
    static {
        jacksonObjectMapper.enable(Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        jacksonObjectMapper.enable(Feature.ALLOW_COMMENTS);
        jacksonObjectMapper.enable(Feature.ALLOW_UNQUOTED_CONTROL_CHARS);

        // jacksonObjectMapper.enable(DeserializationFeature.);
        
        jacksonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    public static FxNode readTree(InputStream in) {
        FxMemRootDocument doc = new FxMemRootDocument();
        readTree(doc.contentWriter(), in);
        return doc.getContent();
    }

    public static FxNode readTree(File in) {
        FxMemRootDocument doc = new FxMemRootDocument();
        readTree(doc.contentWriter(), in);
        return doc.getContent();
    }
    
    public static FxNode readTree(FxChildWriter dest, InputStream in) {
        JsonNode jacksonNode;
        try {
            jacksonNode = jacksonObjectMapper.readTree(in);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to parse as json", ex);
        }
        return Jackson2FxTreeBuilder.buildTree(dest, jacksonNode);
    }

    public static FxNode readTree(FxChildWriter dest, File in) {
        JsonNode jacksonNode;
        try {
            jacksonNode = jacksonObjectMapper.readTree(in);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to parse as json", ex);
        }
        return Jackson2FxTreeBuilder.buildTree(dest, jacksonNode);
    }


    public static void writeTree(OutputStream dest, FxNode tree) throws IOException {
        JsonNode jacksonTree = Jackson2FxTreeBuilder.buildJacksonTree(tree);
        try {
            jacksonObjectMapper.writeValue(dest, jacksonTree);
        }
        catch (JsonGenerationException|JsonMappingException ex) {
            throw new RuntimeException("Failed to write as json", ex);
        }
    }
    
    public static void writeTree(File dest, FxNode tree) {
        JsonNode jacksonTree = Jackson2FxTreeBuilder.buildJacksonTree(tree);
        try {
            jacksonObjectMapper.writeValue(dest, jacksonTree);
        }
        catch (JsonGenerationException|JsonMappingException ex) {
            throw new RuntimeException("Failed to write as json", ex);
        }
        catch(IOException ex) {
            throw new RuntimeException("Failed to write as json to file '" + dest + "'", ex);
        }
    }
    
}
