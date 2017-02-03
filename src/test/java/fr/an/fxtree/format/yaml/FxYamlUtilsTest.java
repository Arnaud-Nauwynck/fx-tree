package fr.an.fxtree.format.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
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

    @Test
    public void testReadTree_inputStream() throws Exception {
        // Prepare
        FxMemRootDocument doc = new FxMemRootDocument();
        File inputFile = new File("src/test/data/yaml/file1.yaml");
        FxChildWriter contentWriter = doc.contentWriter();
        InputStream in = new FileInputStream(inputFile);
        // Perform
        FxYamlUtils.readTree(contentWriter, in);
        // Post-check
        FxNode content = doc.getContent();
        Assert.assertNotNull(content);

        // Perform
        FxNode content2 = FxYamlUtils.readTree(in);
        Assert.assertNotNull(content2);
    }

    @Test
    public void testYamlTextToTree() throws Exception {
        // Prepare
        FxMemRootDocument doc = new FxMemRootDocument();
        File inputFile = new File("src/test/data/yaml/file1.yaml");
        byte[] fileContent = FileUtils.readFileToByteArray(inputFile);
        FxChildWriter contentWriter = doc.contentWriter();
        // Perform
        FxYamlUtils.yamlTextToTree(contentWriter, new String(fileContent));
        // Post-check
        FxNode content = doc.getContent();
        Assert.assertNotNull(content);
        FxNode check = FxYamlUtils.readTree(inputFile);
        String contentText = content.toString();
        Assert.assertEquals(check.toString(), contentText);
    }

    @Test
    public void testTreeToYamlText() throws Exception {
        // Prepare
        File inputFile = new File("src/test/data/yaml/file1.yaml");
        FxNode inputTree = FxYamlUtils.readTree(inputFile);
        // Perform
        String res = FxYamlUtils.treeToYamlText(inputTree);
        // Post-check
        Assert.assertNotNull(res);
        Assert.assertEquals("fieldBoolfalse: false\n" +
            "fieldArray2: [true, 1, Hello]\n" +
            "fieldLong: 1234567890\n" +
            "fieldDouble: 1.2345678E-10\n" +
            "fieldInt: 1\n" +
            "fieldArray: [true, 1, Hello]\n" +
            "fieldStr: Hello World\n" +
            "fieldBoolTrue: true\n" +
            "emptyString: ''\n" +
            "emptyArray: []\n" +
            "emptyObject: {}\n" +
            "fieldObj:\n" +
            "  a: 1\n" +
            "  b: 2\n" +
            "  fieldObjObj: {c: 1}\n", res);
    }

    @Test
    public void testWriteTree() {
     // Prepare
        File inputFile = new File("src/test/data/yaml/file1.yaml");
        FxNode inputTree = FxYamlUtils.readTree(inputFile);
        File testDir = new File("target/test");
        if (! testDir.exists()) testDir.mkdirs();
        File tmpFile = new File(testDir, "file1.yaml");
        // Perform
        FxYamlUtils.writeTree(tmpFile, inputTree);
        // Post-check
        tmpFile.delete();
    }

    @Test
    public void testWriteTree_outputstream() throws Exception {
     // Prepare
        File inputFile = new File("src/test/data/yaml/file1.yaml");
        FxNode inputTree = FxYamlUtils.readTree(inputFile);
        File testDir = new File("target/test");
        if (! testDir.exists()) testDir.mkdirs();
        File tmpFile = new File(testDir, "file1.yaml");
        OutputStream out = new FileOutputStream(tmpFile);
        // Perform
        FxYamlUtils.writeTree(out, inputTree);
        out.close();
        // Post-check
        tmpFile.delete();
    }
}
