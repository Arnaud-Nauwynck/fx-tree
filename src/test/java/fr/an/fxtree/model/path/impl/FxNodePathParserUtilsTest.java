package fr.an.fxtree.model.path.impl;

import org.junit.Assert;
import org.junit.Test;

import fr.an.fxtree.model.path.FxChildPathElement;
import fr.an.fxtree.model.path.FxNodeOuterPath;
import fr.an.fxtree.model.path.FxNodePath;

public class FxNodePathParserUtilsTest {

    @Test
    public void testParse() {
        Assert.assertEquals(FxNodePath.of(FxChildPathElement.thisRoot()), FxNodePathParserUtils.parse("$."));
        Assert.assertEquals(FxNodePath.of("a", "b"), FxNodePathParserUtils.parse(".a.b"));
        Assert.assertEquals(FxNodePath.of(123, -12), FxNodePathParserUtils.parse("[123][-12]"));
    }
    
    @Test
    public void testParseOuterPath() {
        FxNodeOuterPath res;
        res = FxNodePathParserUtils.parseOuterPath("^.a.b");
        Assert.assertEquals(1, res.getParentCount());
        Assert.assertEquals(FxNodePath.of("a", "b"), res.getThenPath());

        res = FxNodePathParserUtils.parseOuterPath("^12.a.b");
        Assert.assertEquals(12, res.getParentCount());
        Assert.assertEquals(FxNodePath.of("a", "b"), res.getThenPath());

        res = FxNodePathParserUtils.parseOuterPath(".a.b");
        Assert.assertEquals(0, res.getParentCount());
        Assert.assertEquals(FxNodePath.of("a", "b"), res.getThenPath());

        res = FxNodePathParserUtils.parseOuterPath("^");
        Assert.assertEquals(1, res.getParentCount());
        Assert.assertEquals(0, res.getThenPath().size());

        res = FxNodePathParserUtils.parseOuterPath("^12");
        Assert.assertEquals(12, res.getParentCount());
        Assert.assertEquals(0, res.getThenPath().size());
    }
    
}
