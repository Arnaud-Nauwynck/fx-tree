package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxUserPhasesProcessFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper(); 

    @Test
    public void testEvalProcessPhases() {
        tstHelper.doTestFile("evalProcessPhases");
    }
    
}
