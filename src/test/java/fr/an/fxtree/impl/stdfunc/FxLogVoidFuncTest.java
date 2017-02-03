package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxLogVoidFuncTest {
    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper();

    @Test
    public void testEvalInvoke() {
        tstHelper.doTestFile("evalLog");
    }

}
