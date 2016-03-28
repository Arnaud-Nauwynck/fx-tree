package fr.an.fxtree.impl.stdfunc;

import org.junit.Assert;

import fr.an.fxtree.format.json.FxJsonUtilsTest;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFuncRegistry;

public class FxEvalFuncTstHelper {

    protected FxNodeFuncRegistry funcRegistry;

    protected FxPhaseRecursiveEvalFunc phase0Func;
    protected FxMemRootDocument destPhase0;
    protected FxChildWriter outPhase0;

    protected FxPhaseRecursiveEvalFunc phase1Func;
    protected FxMemRootDocument destPhase1;
    protected FxChildWriter outPhase1;

    public FxEvalFuncTstHelper() {
        funcRegistry = FxStdFuncs.stdFuncRegistry();
        
        phase0Func = new FxPhaseRecursiveEvalFunc("phase0", funcRegistry);
        destPhase0 = new FxMemRootDocument();
        outPhase0 = destPhase0.contentWriter();
        
        phase1Func = new FxPhaseRecursiveEvalFunc("phase1", funcRegistry);
        destPhase1 = new FxMemRootDocument();
        outPhase1 = destPhase1.contentWriter();
    }
    
    public FxNodeFuncRegistry getFuncRegistry() {
        return funcRegistry;
    }

    public void doTestFile(String evalBaseFilename) {
        doTestFile_Phase01(evalBaseFilename, false);
    }
    
    public void doTestFile_Phase01(String evalBaseFilename) {
        doTestFile_Phase01(evalBaseFilename, true);
    }
    
    public void doTestFile_Phase01(String evalBaseFilename, boolean phase1) {
        String inputFilename = evalBaseFilename + "-input.json";
        String outputFilename = evalBaseFilename + "-expected.json";
        
        // Perform
        FxNode res = doEvalTstFile_phase01(inputFilename, phase1);
        
        // Post-check
        FxNode expected = FxJsonUtilsTest.getJsonTstFile(outputFilename).getContentObj();
        
        String expectedText = expected.toString();
        String resString = res.toString();
        if (! expectedText.equals(resString)) {
            System.out.println("expecting:" + expectedText);
            System.out.println("actual   :" + resString);
            // TODO ... pretty print as json
            
            
            Assert.assertEquals(expectedText, resString);
        }
    }
    
    public FxNode doEvalTstFile_phase01(String inputFilename, boolean phase1) {
        // Prepare
        FxNode src = FxJsonUtilsTest.getJsonTstFile(inputFilename).getContentObj();
        FxNode res = src;
        
        {
            FxEvalContext ctx0 = new FxEvalContext(null, funcRegistry);
            prepareEvalContext(ctx0, false);
            phase0Func.eval(outPhase0, ctx0, res);
            res = destPhase0.getContent();
        }
        
        if (phase1) {
            FxEvalContext ctx1 = new FxEvalContext(null, funcRegistry);
            prepareEvalContext(ctx1, true);
            phase1Func.eval(outPhase1, ctx1, res);
            res = destPhase1.getContent();
        }
        return res;
    }

    public void prepareEvalContext(FxEvalContext ctx, boolean phase1) {
        // overridable, do nothing
    }

}
