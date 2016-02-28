package fr.an.fxtree.impl.stdfunc;

import java.util.Map;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxLetFunc extends FxNodeFunc {

    public static final String NAME = "let";
    
    // ------------------------------------------------------------------------

    public static final FxLetFunc INSTANCE = new FxLetFunc();
    
    private FxLetFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public FxNode eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        FxObjNode vars = FxNodeValueUtils.getObjOrThrow(srcObj, "vars");
        FxNode templateNode = srcObj.get("template");
        if (templateNode == null) {
            return null;
        }
        
        Map<String,FxNode> replVars = vars.fieldsHashMapCopy();
        FxVarsReplaceFunc replaceFunc = new FxVarsReplaceFunc(replVars);
        
        FxEvalContext childCtx = ctx.createChildContext();
        childCtx.putVariableAll(replVars);
        
        FxNode res = replaceFunc.eval(dest, childCtx, templateNode);
        
        return res;
    }
    
    
}
