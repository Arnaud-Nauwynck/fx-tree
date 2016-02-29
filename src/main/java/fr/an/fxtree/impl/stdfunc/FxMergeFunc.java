package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxMergeFunc extends FxNodeFunc {

    public static final String NAME = "merge";
    
    // ------------------------------------------------------------------------

    public static final FxMergeFunc INSTANCE = new FxMergeFunc();
    
    private FxMergeFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        // TODO Auto-generated method stub
    }


    
}
