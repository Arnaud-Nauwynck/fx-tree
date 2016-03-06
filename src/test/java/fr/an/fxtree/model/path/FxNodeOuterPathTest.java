package fr.an.fxtree.model.path;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public class FxNodeOuterPathTest {

    @Test
    public void testSelectOuterFromStack() {
        // Prepare
        FxMemRootDocument doc = new FxMemRootDocument();
        FxObjNode root = doc.contentWriter().addObj();
        FxObjNode aObj = root.putObj("a");
        FxObjNode b1Obj = aObj.putObj("b1");
        FxObjNode c1Obj = b1Obj.putObj("c1");
        FxObjNode b2Obj = aObj.putObj("b2");
        FxObjNode c2Obj = b2Obj.putObj("c2");
        List<FxNode> stackC1 = ImmutableList.of(aObj, b1Obj, c1Obj);
        // Perform
        FxNodeOuterPath outerPath = FxNodeOuterPath.parse("^2.b2.c2");
        FxNode res = outerPath.selectOuterFromStack(stackC1);
        // Post-check
        Assert.assertSame(c2Obj, res);
    }
    
}
