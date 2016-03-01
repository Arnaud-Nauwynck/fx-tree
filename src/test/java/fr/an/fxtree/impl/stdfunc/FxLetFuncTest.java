package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxLetFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper(); 

    @Test
    public void testEvalLet() {
        tstHelper.doTestFile("evalLet");
    }

}
