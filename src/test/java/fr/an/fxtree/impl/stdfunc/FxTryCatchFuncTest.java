package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

public class FxTryCatchFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper();

    @Test
    public void testEvalTryCatch() {
        tstHelper.doTestFile("evalTryCatch");
    }

    /* called by introspection, from test */
    public static void throwRuntimeException(String message, String nestedCauseMessage) {
        throw new RuntimeException(message, new RuntimeException(nestedCauseMessage));
    }
}
