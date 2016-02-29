package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxTryCatchFunc extends FxNodeFunc {

    public static final String NAME = "try-catch";
    
    // ------------------------------------------------------------------------

    public static final FxTryCatchFunc INSTANCE = new FxTryCatchFunc();
    
    private FxTryCatchFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        FxNode templateNode = FxNodeValueUtils.getOrThrow(srcObj, "body");
        FxNode catchNode = FxNodeValueUtils.getOrThrow(srcObj, "catch");
        
        try {
            FxMemRootDocument tmpDoc = new FxMemRootDocument();
            
            FxPhaseRecursiveEvalFunc.recursiveEvalCurrPhaseFunc(tmpDoc.contentWriter(), ctx, templateNode);
            
            // ok, no exception .. copy temp result
            FxNodeCopyVisitor.removeAndCopyContentTo(dest, tmpDoc);
        } catch(Exception ex) {
            FxPhaseRecursiveEvalFunc.recursiveEvalCurrPhaseFunc(dest, ctx, catchNode);
        }
    }
    
    
}
