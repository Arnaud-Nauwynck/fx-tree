package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxForeachFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper();

    @Test
    public void testEvalForeach() {
        tstHelper.doTestFile("evalForeach");
    }

}
