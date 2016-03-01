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
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        FxNode expr = FxNodeValueUtils.getOrThrow(srcObj, "expr");
        FxObjNode whenNodes = FxNodeValueUtils.getObjOrThrow(srcObj, "when");
        
        String exprValue = FxCurrEvalCtxUtil.recurseEvalToString(ctx, expr); 
        FxNode templateNode = whenNodes.get(exprValue);
        if (templateNode == null) {
            templateNode = srcObj.get("default");
        }
        
        if (templateNode != null) {
            FxNodeCopyVisitor.copyTo(dest, templateNode);
        }
    }
    
}
