package fr.an.fxtree.impl.stdfunc;

import java.util.List;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
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
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;

        // resolve arguments: "phases", as string comma separated values, or array of string
        FxNode phasesArg = srcObj.get("phases");
        if (phasesArg == null) return;
        List<String> phases = FxNodeValueUtils.nodeToStringList(phasesArg, true);
        if (phases == null || phases.isEmpty()) return;
        
        FxNode contentSrc = srcObj.get("src");
        if (contentSrc == null) return;
        
        FxPhaseRecursiveEvalFunc.evalPhases(dest, phases, ctx, contentSrc, extraFuncRegistry);
    }

}
