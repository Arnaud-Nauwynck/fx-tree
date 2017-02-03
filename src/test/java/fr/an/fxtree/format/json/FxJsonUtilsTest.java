package fr.an.fxtree.format.json;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.an.fxtree.format.util.FxReaderUtils;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.util.FxNodeAssert;
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

    protected static class FooObj {
        private String foo;
        private int bar;

        public FooObj() {
        }

        public FooObj(String foo, int bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return foo;
        }
        public void setFoo(String foo) {
            this.foo = foo;
        }
        public int getBar() {
            return bar;
        }
        public void setBar(int bar) {
            this.bar = bar;
        }

    }

    @Test
    public void testValueToTree() {
        // Prepare
        FxMemRootDocument doc = new FxMemRootDocument();
        FooObj value = new FooObj("foo", 123);
        // Perform
        FxNode res = FxJsonUtils.valueToTree(doc.contentWriter(), value);
        // Post-check
        Assert.assertEquals("{\"foo\":\"foo\",\"bar\":123}", res.toString());
    }

    @Test
    public void testValueToTree_return() {
        // Prepare
        FooObj value = new FooObj("foo", 123);
        // Perform
        FxNode res = FxJsonUtils.valueToTree(value);
        // Post-check
        Assert.assertEquals("{\"foo\":\"foo\",\"bar\":123}", res.toString());
    }

    @Test
    public void testTreeToValue() {
        // Prepare
        FxNode valueNode = FxJsonUtils.valueToTree(new FooObj("foo", 123));
        // Perform
        FooObj res = FxJsonUtils.treeToValue(FooObj.class, valueNode);
        // Post-check
        Assert.assertEquals("foo", res.getFoo());
        Assert.assertEquals(123, res.getBar());
    }

    @Test
    public void testReadTree_several() {
        // Prepare
        ByteArrayInputStream bufferIn = new ByteArrayInputStream("{id:1} {id:2}".getBytes());
        InputStream forceReadByteOneByOneInputStream = new FilterInputStream(bufferIn) {
            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                // Force read bytes one by one !!! ...
                int resByte;
                try {
                    resByte = super.read();
                } catch(EOFException ex) {
                    return 0;
                }
                b[off] = (byte) resByte;
                return 1;
            }
            @Override
            public int read() throws IOException {
                int res = super.read();
                return res;
            }
        };
        // Perform
        FxNode res0 = FxJsonUtils.readTree(forceReadByteOneByOneInputStream); // consume buffer 0...8000 bytes, even if only 6 bytes are used!
        Assert.assertTrue(res0.isObject());
        // Post-check
        FxNode res1 = FxJsonUtils.readTree(forceReadByteOneByOneInputStream);
        Assert.assertTrue(res1.isObject());
    }

    protected static final boolean DEBUG_wrapParser = false;

    @Test
    public void testCreatePartialParser() throws IOException {
        // Prepare
        InputStream bufferIn = new ByteArrayInputStream("{id:1} {id:2}".getBytes());
        Reader inReader = new InputStreamReader(bufferIn);
        if (DEBUG_wrapParser) inReader = wrapDebugReader(inReader);

        Supplier<FxNode> parserSupplier = FxJsonUtils.createPartialParser(inReader);
        // Perform
        FxNode res0 = parserSupplier.get();
        Assert.assertTrue(res0.isObject());
        FxNodeAssert.assertIntEquals(1, ((FxObjNode) res0).get("id"));
        // Post-check
        FxNode res1 = parserSupplier.get();
        Assert.assertTrue(res1.isObject());
        FxNodeAssert.assertIntEquals(2, ((FxObjNode) res1).get("id"));
        inReader.close();
    }

    @Test
    public void testCreatePartialParser_any() throws IOException {
     // Prepare
        InputStream bufferIn = new ByteArrayInputStream("{id:1} [1,2] true 1234 12.45 \"text\"".getBytes());
        Reader inReader = new InputStreamReader(bufferIn);
        if (DEBUG_wrapParser) inReader = wrapDebugReader(inReader);

        Supplier<FxNode> parserSupplier = FxJsonUtils.createPartialParser(inReader);
        { // Perform
            FxNode res0 = parserSupplier.get();
            // Post-check
            Assert.assertTrue(res0.isObject());
            FxNodeAssert.assertIntEquals(1, ((FxObjNode) res0).get("id"));
        }
        { // Perform
            FxNode res1 = parserSupplier.get();
            // Post-check
            Assert.assertTrue(res1.isArray());
            FxNodeAssert.assertIntEquals(1, ((FxArrayNode) res1).get(0));
            FxNodeAssert.assertIntEquals(2, ((FxArrayNode) res1).get(1));
        }
        { // Perform
            FxNode res2 = parserSupplier.get();
            // Post-check
            FxNodeAssert.assertBoolEquals(true, res2);
        }
        { // Perform
            FxNode res3 = parserSupplier.get();
            // Post-check
            FxNodeAssert.assertIntEquals(1234, res3);
        }
        { // Perform
            FxNode res4 = parserSupplier.get();
            // Post-check
            FxNodeAssert.assertDoubleEquals(12.45, res4, 1e-6);
        }
        { // Perform
            FxNode res5 = parserSupplier.get();
            // Post-check
            FxNodeAssert.assertTextEquals("text", res5);
        }
        inReader.close();
    }

    @Test
    public void testCreatePartialParser_mixed() throws IOException {
        // Prepare
        InputStream bufferIn = new ByteArrayInputStream("..some text obj1={id:1}!..other text obj2={id:2} ..other".getBytes());
        Reader reader = new InputStreamReader(bufferIn);
        if (DEBUG_wrapParser) reader = wrapDebugReader(reader);
        Supplier<FxNode> parserSupplier = FxJsonUtils.createPartialParser(reader);
        // Perform
        String text = FxReaderUtils.readUntil(reader, "=");
        Assert.assertEquals("..some text obj1=", text);
        FxNode res0 = parserSupplier.get();
        Assert.assertTrue(res0.isObject());
        FxNodeAssert.assertIntEquals(1, ((FxObjNode) res0).get("id"));
        String text2 = FxReaderUtils.readUntil(reader, "=");
        Assert.assertEquals("!..other text obj2=", text2);
        FxNode res1 = parserSupplier.get();
        Assert.assertTrue(res1.isObject());
        FxNodeAssert.assertIntEquals(2, ((FxObjNode) res1).get("id"));
        String text3 = FxReaderUtils.readUntil(reader, "?");
        Assert.assertEquals(" ..other", text3);
        reader.close();
    }


    private Reader wrapDebugReader(Reader delegate) {
        return new Reader() {
            int count;
            @Override
			public void close() throws IOException {
                delegate.close();
            }
            @Override
            public int read(char cbuf[], int off, int len) throws IOException {
                int res = 0;
                for(int i = 0; i < len; i++) {
                    int resCh;
                    try {
                        resCh = delegate.read();
                    } catch(EOFException ex) {
                        if (res == 0) {
                            res = -1;
                        }
                        break;
                    }
                    res++;
                    cbuf[off+i] = (char) resCh;
                    count++;
                    checkCount((char) resCh);
                }
                return res;
            }
            @Override
            public int read() throws IOException {
                int res = delegate.read();
                count++;
                checkCount((char) res);
                return res;
            }
            private void checkCount(char resChar) {
                if (resChar == -1) {
                    return;
                }
                if (resChar == '=') {
                    debug_noop();
                }
                if (resChar == '}') {
                    debug_noop();
                }
                if (resChar == '!') {
                    debug_noop();
                }
                System.out.println("read: " + resChar);
                if (count == 6) {
                    debug_noop();
                }
            }
            private void debug_noop() {
            }
        };
    }

}
