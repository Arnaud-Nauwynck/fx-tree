package fr.an.fxtree.impl.helper;

import java.io.File;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.util.FxNodeAssert;
import fr.an.fxtree.model.FxIntNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public class FxKeyNodeFileStoreTest {

    protected FxKeyNodeFileStore sut;

    @Before
    public void setup() {
        File dir = new File("target/test");
        if (! dir.exists()) {
            dir.mkdirs();
        }
        File storeFile = new File(dir, "test-keyNodeFileStore.yaml");
        sut = new FxKeyNodeFileStore(storeFile);
    }

    @Test
    public void testPut_get_remove_reload() {
        FxObjNode objNode = new FxMemRootDocument().setContentObj();
        FxIntNode value123 = objNode.put("value123", 123);
        sut.put("key1", value123);

        FxObjNode obj1 = objNode.putObj("obj1");
        obj1.put("field1", "value1");
        sut.put("key2", obj1);

        FxNode obj1Ref = sut._unsafeGet("key2");
        Assert.assertNotNull(obj1Ref);
        Assert.assertNotSame(obj1, obj1Ref);

        Set<String> ks = sut.keySet();
        Assert.assertEquals(ImmutableSet.of("key1", "key2"), ks);

        Assert.assertTrue(sut.containsKey("key1"));
        Assert.assertFalse(sut.containsKey("unknnown-key"));

        FxNode value123Copy = sut.getCopy("key1");
        FxNodeAssert.assertEquals(value123, value123Copy);

        FxNode obj1Copy = sut.getCopy("key2");
        FxNodeAssert.assertEquals(obj1, obj1Copy);

        FxMemRootDocument tmpDoc = new FxMemRootDocument();
        sut.getCopyTo(tmpDoc.contentWriter(), "key2");
        FxNode obj1Copy2 = tmpDoc.getContent();
        FxNodeAssert.assertEquals(obj1, obj1Copy2);

        sut.remove("key1");
        FxNode value123Copy_null = sut.getCopy("key1");
        Assert.assertNull(value123Copy_null);

        FxKeyNodeFileStore sutReload = new FxKeyNodeFileStore(sut.getStoreFile());
        FxNodeAssert.assertEquals(obj1, sutReload.getCopy("key2"));

        Assert.assertNotNull(sut.toString());
    }

}
