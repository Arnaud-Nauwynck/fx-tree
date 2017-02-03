package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxRandomIntFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper();

    @Test
    public void testEvalRandomInt() {
        tstHelper.doTestFile("eval-RandomInt");
    }
}
