package fr.an.fxtree.format.json;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.an.fxtree.format.json.FxJsonUtils;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public class FxJsonUtilsTest {

    public static FxMemRootDocument getJsonTstFile(String fileName) {
        FxMemRootDocument doc = new FxMemRootDocument(); 
        File inFile = new File("src/test/data/json/" + fileName);
        // Perform
        FxJsonUtils.readTree(doc.contentWriter(), inFile);
        return doc;
    }
    
    @Test
    public void testReadTree() throws Exception {
        // Prepare
        FxMemRootDocument doc = new FxMemRootDocument(); 
        File inFile = new File("src/test/data/json/file1.json");
        // Perform
        FxJsonUtils.readTree(doc.contentWriter(), inFile);
        FxNode content = doc.getContent();
        // Post-check
        Assert.assertNotNull(content);
        String contentStr = content.toString();
        ObjectMapper jacksonOM = new ObjectMapper();
        JsonNode reReadTree = jacksonOM.readTree(contentStr);
        JsonNode origTree = jacksonOM.readTree(contentStr);
        Assert.assertTrue(reReadTree.equals(origTree));
        Assert.assertEquals(contentStr, origTree.toString());
        
        
        FxObjNode r = (FxObjNode) content; 
        Assert.assertEquals(true, FxNodeValueUtils.nodeToBoolean(r.get("fieldBoolTrue")));
        Assert.assertEquals(false, FxNodeValueUtils.nodeToBoolean(r.get("fieldBoolfalse")));
        Assert.assertEquals(1, FxNodeValueUtils.nodeToInt(r.get("fieldInt")));
        Assert.assertEquals(1234567890, FxNodeValueUtils.nodeToLong(r.get("fieldLong")));
        Assert.assertEquals(1.2345678e-10, FxNodeValueUtils.nodeToDouble(r.get("fieldDouble")), 1e-10);
        Assert.assertEquals("Hello World", FxNodeValueUtils.nodeToString(r.get("fieldStr")));
        FxObjNode rFieldObj = FxNodeValueUtils.nodeToObj(r.get("fieldObj"));
        Assert.assertNotNull(rFieldObj);
        Assert.assertEquals(1, FxNodeValueUtils.nodeToInt(rFieldObj.get("a")));
        Assert.assertEquals(2, FxNodeValueUtils.nodeToInt(rFieldObj.get("b")));
        FxObjNode rFieldObjObj = FxNodeValueUtils.nodeToObj(rFieldObj.get("fieldObjObj"));
        Assert.assertNotNull(rFieldObjObj);
        Assert.assertEquals(1, FxNodeValueUtils.nodeToInt(rFieldObjObj.get("c")));        
        FxArrayNode rFieldArray = FxNodeValueUtils.nodeToArray(r.get("fieldArray"));
        Assert.assertEquals(3, rFieldArray.size());
        Assert.assertEquals(true, FxNodeValueUtils.nodeToBoolean(rFieldArray.get(0)));
        Assert.assertEquals(1, FxNodeValueUtils.nodeToInt(rFieldArray.get(1)));
        Assert.assertEquals("Hello", FxNodeValueUtils.nodeToString(rFieldArray.get(2)));
        FxArrayNode emptyArray = FxNodeValueUtils.nodeToArray(r.get("emptyArray"));
        Assert.assertEquals(0, emptyArray.size());
        FxObjNode emptyObj = FxNodeValueUtils.nodeToObj(r.get("emptyObject"));
        Assert.assertEquals(0, emptyObj.size());
        Assert.assertEquals("", FxNodeValueUtils.nodeToString(r.get("emptyString")));
    }
}