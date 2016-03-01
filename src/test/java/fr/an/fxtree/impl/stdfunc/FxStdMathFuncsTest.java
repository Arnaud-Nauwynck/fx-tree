package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxStdMathFuncsTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper(); 

    @Test
    public void testEvalStdMathFuncs() {
        tstHelper.doTestFile("evalStdMathFuncs");
    }

    @Test
    public void testEvalStdMathFuncsRecursive() {
        tstHelper.doTestFile("evalStdMathFuncsRecursive");
    }

}
