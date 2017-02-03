package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxThrowFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper();

    @Test
    public void testEvalSwitch() {
        tstHelper.doTestFile("evalThrow");
    }
}
