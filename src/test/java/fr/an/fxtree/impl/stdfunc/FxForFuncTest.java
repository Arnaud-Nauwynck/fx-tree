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

    @Test // TODO recursive eval not implemented yet ...
    public void testEvalForNestedRecursive() {
        tstHelper.doTestFile("evalForNestedRecursive");
    }

}
