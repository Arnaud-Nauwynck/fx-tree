package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxIfFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper(); 

    @Test
    public void testEvalIf() {
        tstHelper.doTestFile("evalIf");
    }
    
    @Test
    public void testEvalIfRecursivePhase() {
        tstHelper.doTestFile("evalIf-Recursive");
    }
}
