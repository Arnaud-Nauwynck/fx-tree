package fr.an.fxtree.json;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxNode;

public class FxJsonUtilsTest {

    public static FxMemRootDocument getJsonTstFile(String fileName) {
        FxMemRootDocument doc = new FxMemRootDocument(); 
        File inFile = new File("src/test/data/json/" + fileName);
        // Perform
        FxJsonUtils.readTree(doc, inFile);
        return doc;
    }
    
    @Test
    public void testReadTree() throws Exception {
        // Prepare
        FxMemRootDocument doc = new FxMemRootDocument(); 
        File inFile = new File("src/test/data/json/file1.json");
        // Perform
        FxJsonUtils.readTree(doc, inFile);
        FxNode content = doc.getContent();
        // Post-check
        Assert.assertNotNull(content);
        String contentStr = content.toString();
        ObjectMapper jacksonOM = new ObjectMapper();
        JsonNode reReadTree = jacksonOM.readTree(contentStr);
        JsonNode origTree = jacksonOM.readTree(contentStr);
        Assert.assertTrue(reReadTree.equals(origTree));
        Assert.assertEquals(contentStr, origTree.toString());
    }
}
