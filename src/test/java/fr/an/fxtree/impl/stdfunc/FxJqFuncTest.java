package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxJqFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper(); 

    @Test
    public void testEvalJq() {
        tstHelper.doTestFile("evalJq");
    }

}
