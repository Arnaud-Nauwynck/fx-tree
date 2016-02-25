package fr.an.fxtree.impl.stdfunc;

import org.junit.Assert;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.json.FxJsonUtilsTest;
import fr.an.fxtree.model.FxChildAdder;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFuncRegistry;

public class FxEvalFuncTstHelper {

    protected FxPhaseRecursiveEvalFunc phase0Func;
    
    protected FxMemRootDocument destPhase0;
    protected FxChildAdder outPhase0;

    public FxEvalFuncTstHelper() {
        FxNodeFuncRegistry funcRegistry = FxStdFuncs.stdFuncRegistry();
        
        phase0Func = new FxPhaseRecursiveEvalFunc("phase0", funcRegistry);
        destPhase0 = new FxMemRootDocument();
        outPhase0 = destPhase0.contentAdder();
    }

    public void doTestFile(String evalBaseFilename) {
        String inputFilename = evalBaseFilename + "-input.json";
        String outputFilename = evalBaseFilename + "-expected.json";
        
        // Perform
        FxNode res = doEval0TstFile(inputFilename);
        
        // Post-check
        FxNode expected = FxJsonUtilsTest.getJsonTstFile(outputFilename).getContentObj();
        Assert.assertEquals(expected.toString(), res.toString());
    }

    public FxNode doEval0TstFile(String inputFilename) {
        // Prepare
        FxNode src = FxJsonUtilsTest.getJsonTstFile(inputFilename).getContentObj();

        FxEvalContext ctx = new FxEvalContext(null, null);
        phase0Func.eval(outPhase0, ctx, src);
        FxNode resPhase0 = destPhase0.getContent();

        return resPhase0;
    }
    
}
