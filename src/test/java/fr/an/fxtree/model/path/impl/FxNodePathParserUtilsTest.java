package fr.an.fxtree.model.path.impl;

import org.junit.Assert;
import org.junit.Test;

import fr.an.fxtree.model.path.FxChildPathElement;
import fr.an.fxtree.model.path.FxNodePath;

public class FxNodePathParserUtilsTest {

    @Test
    public void testParse() {
        Assert.assertEquals(FxNodePath.of(FxChildPathElement.thisRoot()), FxNodePathParserUtils.parse("$."));
        Assert.assertEquals(FxNodePath.of("a", "b"), FxNodePathParserUtils.parse(".a.b"));
        Assert.assertEquals(FxNodePath.of(123, -12), FxNodePathParserUtils.parse("[123][-12]"));
    }
}
