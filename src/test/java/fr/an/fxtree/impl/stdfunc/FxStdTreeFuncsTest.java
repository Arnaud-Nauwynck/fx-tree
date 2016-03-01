package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxStdTreeFuncsTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper(); 

    @Test
    public void testEvalStdTreeFuncs() {
        tstHelper.doTestFile("evalStdTreeFuncs-TreeCopy");
    }
}
