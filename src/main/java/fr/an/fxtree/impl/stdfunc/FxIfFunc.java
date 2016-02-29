package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxIfFunc extends FxNodeFunc {

    public static final String NAME = "if";
    
    // ------------------------------------------------------------------------

    public static final FxIfFunc INSTANCE = new FxIfFunc();
    
    private FxIfFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        boolean expr = FxNodeValueUtils.getBooleanOrThrow(srcObj, "expr");
        // TOADD ... may expose Eager dependency graph "expr" -> "then",  of "expr" -> "else" 
        FxNode templateNode;
        if (expr) {
            templateNode = srcObj.get("then");
        } else {
            templateNode = srcObj.get("else");
        }

        if (templateNode != null) {
            FxNodeCopyVisitor.copyTo(dest, templateNode);
        }
    }
    
    
}
