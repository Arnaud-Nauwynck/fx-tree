package fr.an.fxtree.format.yaml;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public class FxYamlUtilsTest {

    @Test
    public void testReadTree() {
        // Prepare
        FxMemRootDocument doc = new FxMemRootDocument(); 
        File inputFile = new File("src/test/data/yaml/file1.yaml");
        FxChildWriter contentWriter = doc.contentWriter();
        // Perform
        FxYamlUtils.readTree(contentWriter, inputFile);
        FxNode content = doc.getContent();
        // Post-check
        Assert.assertNotNull(content);
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
