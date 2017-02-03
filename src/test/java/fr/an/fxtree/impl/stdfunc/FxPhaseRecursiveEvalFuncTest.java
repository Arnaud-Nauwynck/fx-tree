package fr.an.fxtree.impl.stdfunc;

import org.junit.Assert;
import org.junit.Test;

import fr.an.fxtree.format.json.FxJsonUtilsTest;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFuncRegistry;

public class FxPhaseRecursiveEvalFuncTest {

    FxNodeFuncRegistry funcRegistry = FxStdFuncs.stdFuncRegistry();
    protected FxPhaseRecursiveEvalFunc sutPhase0 = new FxPhaseRecursiveEvalFunc("phase0", funcRegistry);
    protected FxPhaseRecursiveEvalFunc sutPhase1 = new FxPhaseRecursiveEvalFunc("phase1", funcRegistry);

    protected FxMemRootDocument destPhase0 = new FxMemRootDocument();
    protected FxChildWriter outPhase0 = destPhase0.contentWriter();

    protected FxMemRootDocument destPhase1 = new FxMemRootDocument();
    protected FxChildWriter outPhase1 = destPhase1.contentWriter();

    @Test
    public void testEval1() {
        doTestEvalFile("eval1");
    }

    @Test
    public void testEval2() {
        doTestEvalFile("eval2");
    }

    private void doTestEvalFile(String evalBaseFilename) {
        // Prepare
        String inputFilename = evalBaseFilename + "-input.json";
        String outputFilename = evalBaseFilename + "-expected.json";
        FxNode src = FxJsonUtilsTest.getJsonTstFile(inputFilename).getContentObj();
        FxEvalContext ctx = new FxEvalContext(null, funcRegistry);
        // Perform
        sutPhase0.eval(outPhase0, ctx, src);
        FxNode resPhase0 = destPhase0.getContent();

        sutPhase1.eval(outPhase1, ctx, resPhase0);
        FxNode resPhase1 = destPhase1.getContent();

        // Post-check
        FxNode expected = FxJsonUtilsTest.getJsonTstFile(outputFilename).getContentObj();
        Assert.assertEquals(expected.toString(), resPhase1.toString());
    }

}
