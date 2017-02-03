package fr.an.fxtree.model.path;

import org.junit.Assert;
import org.junit.Test;

import fr.an.fxtree.format.json.FxJsonUtilsTest;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public class FxNodePathTest {

    private static final FxNodePath path_ab12 = FxNodePath.of("a", "b", 1, 2);

    @Test
    public void testToString() {
        Assert.assertEquals(".a.b[1][2]", path_ab12.toString());
    }

    @Test
    public void testEquals() {
        FxNodePath copypath_ab12 = FxNodePath.of("a", "b", 1, 2);
        Assert.assertEquals(copypath_ab12, path_ab12);
        Assert.assertEquals(copypath_ab12.hashCode(), path_ab12.hashCode());
    }

    @Test
    public void testParent() {
        FxNodePath path_ab1 = path_ab12.parent();
        Assert.assertEquals(".a.b[1]", path_ab1.toString());
        FxNodePath copy_path_ab1 = path_ab12.parent();
        Assert.assertEquals(path_ab1, copy_path_ab1);
    }

    @Test
    public void testChild() {
        FxNodePath path_ab12ab12 = path_ab12.child(path_ab12);
        Assert.assertEquals(".a.b[1][2].a.b[1][2]", path_ab12ab12.toString());

        FxNodePath path_ab12a = path_ab12.child("a");
        Assert.assertEquals(".a.b[1][2].a", path_ab12a.toString());

        FxNodePath path_ab123 = path_ab12.child(3);
        Assert.assertEquals(".a.b[1][2][3]", path_ab123.toString());
    }

    @Test
    public void testSelect() {
        FxMemRootDocument jsonDoc = FxJsonUtilsTest.getJsonTstFile("file1.json");
        FxNode content = jsonDoc.getContent();
//        {
//            "fieldBoolTrue": true,
//            "fieldBoolfalse": false,
//            "fieldInt": 1,
//            "fieldLong": 1234567890,
//            "fieldDouble": 1.2345678e-10,
//            "fieldStr": "Hello World",
//
//            "fieldObj": {
//                "a": 1,
//                "b": 2,
//                "fieldObjObj": {
//                }
//            },
//
//            "fieldArray": [ true, 1, "Hello" ]
//
//        }

        FxNode res = FxNodePath.of("fieldBoolTrue").select(content);
        FxObjNode contentObj = (FxObjNode) content;
        Assert.assertSame(contentObj.get("fieldBoolTrue"), res);

        res = FxNodePath.of("fieldObj", "a").select(content);
        FxObjNode fieldObj = (FxObjNode) contentObj.get("fieldObj");
        Assert.assertSame(fieldObj.get("a"), res);

        res = FxNodePath.of("fieldArray", 0).select(content);
        FxArrayNode fieldArray = (FxArrayNode) contentObj.get("fieldArray");
        Assert.assertSame(fieldArray.get(0), res);

        Assert.assertSame(fieldArray.get(1), FxNodePath.of("fieldArray", 1).select(content));
        Assert.assertSame(fieldArray.get(2), FxNodePath.of("fieldArray", -1).select(content));

        Assert.assertNull(FxNodePath.of("unknown").select(content));
        Assert.assertNull(FxNodePath.of(123).select(content));
    }
}
