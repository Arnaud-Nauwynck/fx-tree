package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
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
    
    /** optional extra funcRegistry ... cf FxEvalContext.funcRegistry */
    private FxNodeFuncRegistry extraFuncRegistry;
    
    // ------------------------------------------------------------------------
    
    public FxUserPhasesProcessFunc() {
        this(null);
    }
    
    public FxUserPhasesProcessFunc(FxNodeFuncRegistry extraFuncRegistry) {
        this.extraFuncRegistry = extraFuncRegistry;
    }
    
    // ------------------------------------------------------------------------

    @Override
    public FxNode eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;

        // resolve arguments: "phases", as string comma separated values, or array of string
        FxNode phasesArg = srcObj.get("phases");
        if (phasesArg == null) return null;
        String[] phases = FxNodeValueUtils.nodeToStringArray(phasesArg, true);
        if (phases == null || phases.length == 0) return null;
        
        FxNode contentSrc = srcObj.get("src");
        if (contentSrc == null) return null;
        
        // TODO use childCtx + recursive eval ??
        
        FxNode currPhaseRes = contentSrc;
        final int intermediatePhaseLen = phases.length - 1;
        for(int i = 0; i < intermediatePhaseLen; i++) {
            String phase = phases[i];
            FxMemRootDocument tmpResDoc = new FxMemRootDocument();
            FxChildWriter tmpResAdder = tmpResDoc.contentWriter();
            
            FxPhaseRecursiveEvalFunc phaseFunc = new FxPhaseRecursiveEvalFunc(phase, extraFuncRegistry);
            currPhaseRes = phaseFunc.eval(tmpResAdder, ctx, currPhaseRes);
        }

        String lastPhase = phases[phases.length - 1];
        FxPhaseRecursiveEvalFunc lastPhaseFunc = new FxPhaseRecursiveEvalFunc(lastPhase, extraFuncRegistry);
        currPhaseRes = lastPhaseFunc.eval(dest, ctx, currPhaseRes);
        
        return currPhaseRes; 
    }
    
}
