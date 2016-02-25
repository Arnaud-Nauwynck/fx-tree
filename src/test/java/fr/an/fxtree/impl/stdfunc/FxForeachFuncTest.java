package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxForeachFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper(); 

    @Test // TODO does not work yet... cf NOT IMPLEMENTED YET : should use copyReplaceIterVisitor
    public void testEvalForeach() {
        tstHelper.doTestFile("evalForeach");
    }
    
}
