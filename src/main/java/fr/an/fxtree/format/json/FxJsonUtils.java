package fr.an.fxtree.format.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.an.fxtree.format.json.jackson.Jackson2FxTreeBuilder;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

/**
 * JSon<->FxTree converter utility (delegating to jackson lib)
 *
 */
public final class FxJsonUtils {

    private FxJsonUtils() {
    }
    
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
        return Jackson2FxTreeBuilder.jsonNodeToFxTree(dest, jacksonNode);
    }

    public static FxNode readTree(FxChildWriter dest, File in) {
        JsonNode jacksonNode;
        try {
            jacksonNode = jacksonObjectMapper.readTree(in);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to parse as json", ex);
        }
        return Jackson2FxTreeBuilder.jsonNodeToFxTree(dest, jacksonNode);
    }


    public static void writeTree(OutputStream dest, FxNode tree) throws IOException {
        JsonNode jacksonTree = Jackson2FxTreeBuilder.fxTreeToJsonNode(tree);
        try {
            jacksonObjectMapper.writeValue(dest, jacksonTree);
        }
        catch (JsonGenerationException|JsonMappingException ex) {
            throw new RuntimeException("Failed to write as json", ex);
        }
    }
    
    public static void writeTree(File dest, FxNode tree) {
        JsonNode jacksonTree = Jackson2FxTreeBuilder.fxTreeToJsonNode(tree);
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
    
    public static FxNode jsonTextToTree(String jsonText) {
        FxMemRootDocument doc = new FxMemRootDocument();
        jsonTextToTree(doc.contentWriter(), jsonText);
        return doc.getContent();
    }
    
    public static FxNode jsonTextToTree(FxChildWriter dest, String jsonText) {
        ByteArrayInputStream in = new ByteArrayInputStream(jsonText.getBytes()); 
        return readTree(dest, in);
    }
    
    public static String treeToJsonText(FxNode tree) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            writeTree(bout, tree);
        } catch(IOException ex) {
            throw new RuntimeException("Should not occur: failed to format tree to json text", ex);
        }
        return bout.toString();
    }

    // converter for POJO <-> FxNode, using Jackson valueToTree()/treeToValue() then json<->FxTree
    // ------------------------------------------------------------------------
    
    public static FxNode valueToTree(Object value) {
        FxMemRootDocument doc = new FxMemRootDocument();
        valueToTree(doc.contentWriter(), value);
        return doc.getContent();
    }
    
    public static FxNode valueToTree(FxChildWriter dest, Object value) {
        JsonNode jsonNode = jacksonObjectMapper.valueToTree(value);
        return Jackson2FxTreeBuilder.jsonNodeToFxTree(dest, jsonNode);
    }
    
    public static <T> T treeToValue(Class<T> destClass, FxNode tree) {
        JsonNode jsonNode = Jackson2FxTreeBuilder.fxTreeToJsonNode(tree);
        try {
            return jacksonObjectMapper.treeToValue(jsonNode, destClass);
        } catch(Exception ex) {
            throw new RuntimeException("Failed to convert tree to value (using jackson ObjectMapper)", ex);
        }
    }
    
    // ------------------------------------------------------------------------

    /**
     * @param reader input chars to read
     * @return parser (as supplier<FxNode) for parsing next chars until a FxNode is detected
     */
    public static Supplier<FxNode> createPartialParser(Reader reader) {
        // force wrapping reader as one-by-one char Reader, to avoid read buffering 0...8000 so consuming too much chars! 
        Reader inReader = wrapForceReadOneByOneCharReader(reader);
        JsonParser parser;
        try {
            JsonFactory jsonFactory = jacksonObjectMapper.getFactory();
            parser = jsonFactory.createParser(inReader);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Supplier<FxNode> res = () -> {
            try {
                JsonNode jsonNode = parser.readValueAsTree();
                if (jsonNode == null) {
                    return null;
                }
                FxMemRootDocument doc = new FxMemRootDocument();
                Jackson2FxTreeBuilder.jsonNodeToFxTree(doc.contentWriter(), jsonNode);
                return doc.getContent();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        };
        return res;
    }
    
    private static Reader wrapForceReadOneByOneCharReader(Reader delegate) {
        return new Reader() {
            public void close() throws IOException {
                // do nothing!
            }
            @Override
            public int read(char cbuf[], int off, int len) throws IOException {
                int resCh;
                try {
                    resCh = delegate.read();
                } catch(EOFException ex) {
                    return -1;
                }
                cbuf[off] = (char) resCh;
                return 1;
            }
            @Override
            public int read() throws IOException {
                return delegate.read();
            }
        };            
    }

}
