package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxThrowFunc extends FxNodeFunc {
        
    public static final String NAME = "throw";
    
    // ------------------------------------------------------------------------

    public static final FxThrowFunc INSTANCE = new FxThrowFunc();
    
    private FxThrowFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public FxNode eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        String message = FxNodeValueUtils.getStringOrThrow(srcObj, "message");

        throw new FxThrowFuncException(message);
        
    }

    public static class FxThrowFuncException extends RuntimeException {

        /** */
        private static final long serialVersionUID = 1L;

        public FxThrowFuncException(String message) {
            super(message);
        }
        
    }
}
