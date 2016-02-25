package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildAdder;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxNodeFunc;
import fr.an.fxtree.model.func.FxNodeFuncRegistry;

/**
 * clone a tree and evaluate recursively all functions for all input phases list
 * 
 * <PRE>
 * {
 * 
 * }
 * </PRE>
 */
public class FxUserPhasesProcessFunc extends FxNodeFunc {

    public static final String NAME = "process-phases";
    
    private FxNodeFuncRegistry funcRegistry;
    
    // ------------------------------------------------------------------------
    
    public FxUserPhasesProcessFunc(FxNodeFuncRegistry funcRegistry) {
        this.funcRegistry = funcRegistry;
    }
    
    // ------------------------------------------------------------------------

    @Override
    public FxNode eval(FxChildAdder dest, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;

        // resolve arguments: "phases", as string comma separated values, or array of string
        FxNode phasesArg = srcObj.get("phases");
        if (phasesArg == null) return null;
        String[] phases = FxNodeValueUtils.extractStringArray(phasesArg, true);
        if (phases == null || phases.length == 0) return null;
        
        FxNode contentSrc = srcObj.get("src");
        if (contentSrc == null) return null;
        
        FxNode currPhaseRes = contentSrc;
        final int intermediatePhaseLen = phases.length - 1;
        for(int i = 0; i < intermediatePhaseLen; i++) {
            String phase = phases[i];
            FxMemRootDocument tmpResDoc = new FxMemRootDocument();
            FxChildAdder tmpResAdder = tmpResDoc.contentAdder();
            
            FxPhaseRecursiveEvalFunc phaseFunc = new FxPhaseRecursiveEvalFunc(phase, funcRegistry);
            currPhaseRes = phaseFunc.eval(tmpResAdder, currPhaseRes);
        }

        String lastPhase = phases[phases.length - 1];
        FxPhaseRecursiveEvalFunc lastPhaseFunc = new FxPhaseRecursiveEvalFunc(lastPhase, funcRegistry);
        currPhaseRes = lastPhaseFunc.eval(dest, currPhaseRes);
        
        return currPhaseRes; 
    }
    
}
