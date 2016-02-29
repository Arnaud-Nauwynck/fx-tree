package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxStdCtxFuncsTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper(); 

    @Test
    public void testEvalCtxVar() {
        tstHelper.doTestFile("evalStdCtxFuncs");
    }
    
}
