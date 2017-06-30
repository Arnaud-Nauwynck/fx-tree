package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxMergeFuncTest {

	protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper();

    @Test
    public void testEvalMerge() {
        tstHelper.doTestFile("eval-merge");
    }

}
