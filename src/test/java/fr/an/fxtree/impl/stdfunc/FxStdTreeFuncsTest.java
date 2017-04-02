package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxStdTreeFuncsTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper();

    @Test
    public void testEval_TreeCopy() {
        tstHelper.doTestFile("eval-TreeCopy");
    }

    @Test
    public void testEval_TreeMerge() {
        tstHelper.doTestFile("eval-TreeMerge");
    }

    @Test
    public void testEval_MergeDefaults() {
        tstHelper.doTestFile("eval-mergeDefaults");
    }

    @Test
    public void testEval_performCopyDecls() {
        tstHelper.doTestFile("eval-performCopyDecls");
    }

    @Test
    public void testEval_inlineArrayElements() {
        tstHelper.doTestFile("eval-inlineArrayElements");
    }
    
    @Test
    public void testEval_inlineObjectFields() {
        tstHelper.doTestFile("eval-inlineObjectFields");
    }
    
}
