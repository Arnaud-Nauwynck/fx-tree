package fr.an.fxtree.impl.stdfunc;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.json.FxJsonUtilsTest;
import fr.an.fxtree.model.FxChildAdder;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;
import fr.an.fxtree.model.func.FxNodeFuncRegistry;

public class FxPhaseRecursiveEvalFuncTest {

    protected FxPhaseRecursiveEvalFunc sutPhase0;
    protected FxPhaseRecursiveEvalFunc sutPhase1;
    
    protected FxMemRootDocument destPhase0 = new FxMemRootDocument();
    protected FxChildAdder outPhase0 = destPhase0.contentAdder();

    protected FxMemRootDocument destPhase1 = new FxMemRootDocument();
    protected FxChildAdder outPhase1 = destPhase1.contentAdder();

    @Before
    public void setup() {
        Map<String, FxNodeFunc> funcs = new HashMap<String,FxNodeFunc>();
        FxStdMathFuncs.registerBuiltinFuncs(funcs);
        sutPhase0 = new FxPhaseRecursiveEvalFunc("phase0", new FxNodeFuncRegistry(funcs));
        sutPhase1 = new FxPhaseRecursiveEvalFunc("phase1", new FxNodeFuncRegistry(funcs));
    }
    
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
        FxEvalContext ctx = new FxEvalContext(null, null);
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
