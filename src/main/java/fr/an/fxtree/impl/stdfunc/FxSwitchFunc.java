package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxSwitchFunc extends FxNodeFunc {

    public static final String NAME = "switch";
    
    // ------------------------------------------------------------------------

    public static final FxSwitchFunc INSTANCE = new FxSwitchFunc();
    
    private FxSwitchFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public FxNode eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        String expr = FxNodeValueUtils.getStringOrThrow(srcObj, "expr");
        FxObjNode whenNodes = FxNodeValueUtils.getObjOrThrow(srcObj, "when");
        FxNode whenDefaultNode = srcObj.get("default");

        // TOADD ... may expose Eager dependency graph "expr" -> "when.<<expr>>" or "expr" -> "default" 
        FxNode templateNode = whenNodes.get(expr);
        if (templateNode == null) {
            templateNode = whenDefaultNode;
        }
        
        FxNode res = null;
        if (templateNode != null) {
            res = FxNodeCopyVisitor.copyTo(dest, templateNode);
        }
        
        return res;
    }
    
}
