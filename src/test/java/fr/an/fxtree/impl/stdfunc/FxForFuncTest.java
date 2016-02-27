package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxForFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper(); 

    @Test
    public void testEvalFor() {
        tstHelper.doTestFile("evalFor");
    }

    @Test
    public void testEvalForNested() {
        tstHelper.doTestFile_Phase01("evalForNested", true);
    }

    @Test
    public void testEvalForNestedRecursive() {
        tstHelper.doTestFile("evalForNestedRecursive");
    }

}
