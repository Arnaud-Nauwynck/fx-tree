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
import fr.an.fxtree.model.func.FxNodeFunc;
import fr.an.fxtree.model.func.FxNodeFuncRegistry;

public class FxPhaseRecursiveEvalFuncTest {

    protected FxPhaseRecursiveEvalFunc sut;
    
    protected FxMemRootDocument dest = new FxMemRootDocument();
    protected FxChildAdder destOut = dest.contentAdder();
    
    @Before
    public void setup() {
        String phase = "phase0";
        Map<String, FxNodeFunc> funcs = new HashMap<String,FxNodeFunc>();
        FxStdMathFuncs.registerBuiltinFuncs(funcs);
        sut = new FxPhaseRecursiveEvalFunc(phase, new FxNodeFuncRegistry(funcs));
    }
    
    @Test
    public void testEval1() {
        doTestEvalFile("eval1");
    }

    private void doTestEvalFile(String evalBaseFilename) {
        // Prepare
        String inputFilename = evalBaseFilename + "-input.json";
        String outputFilename = evalBaseFilename + "-expected.json";
        FxNode src = FxJsonUtilsTest.getJsonTstFile(inputFilename).getContentObj();
        // Perform
        sut.eval(destOut, src);
        // Post-check
        FxNode res = dest.getContent();
        FxNode expected = FxJsonUtilsTest.getJsonTstFile(outputFilename).getContentObj();
        Assert.assertEquals(expected.toString(), res.toString());
    }
}
