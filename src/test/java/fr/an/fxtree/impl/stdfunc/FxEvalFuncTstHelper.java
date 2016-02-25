package fr.an.fxtree.impl.stdfunc;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.json.FxJsonUtilsTest;
import fr.an.fxtree.model.FxChildAdder;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.func.FxNodeFunc;
import fr.an.fxtree.model.func.FxNodeFuncRegistry;

public class FxEvalFuncTstHelper {

    protected FxPhaseRecursiveEvalFunc phase0Func;
    
    protected FxMemRootDocument destPhase0;
    protected FxChildAdder outPhase0;

    public FxEvalFuncTstHelper() {
        Map<String, FxNodeFunc> funcs = new HashMap<String,FxNodeFunc>();
        FxNodeFuncRegistry funcRegistry = new FxNodeFuncRegistry(funcs);
        
        FxStdMathFuncs.registerBuiltinFuncs(funcs);
        funcs.put(FxUserPhasesProcessFunc.NAME, new FxUserPhasesProcessFunc(funcRegistry)); // chicken and egg dilemna..
        
        phase0Func = new FxPhaseRecursiveEvalFunc("phase0", new FxNodeFuncRegistry(funcs));
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

        phase0Func.eval(outPhase0, src);
        FxNode resPhase0 = destPhase0.getContent();

        return resPhase0;
    }
    
}
