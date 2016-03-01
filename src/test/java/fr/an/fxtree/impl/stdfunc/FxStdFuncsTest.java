package fr.an.fxtree.impl.stdfunc;

import org.junit.Assert;
import org.junit.Test;

import fr.an.fxtree.model.func.FxNodeFuncRegistry;

public class FxStdFuncsTest {

    @Test
    public void testStdFuncRegistry() {
        FxNodeFuncRegistry res = FxStdFuncs.stdFuncRegistry();
        Assert.assertNotNull(res);
    }
}
