package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.model.FxChildAdder;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxMergeFunc extends FxNodeFunc {

    public static final String NAME = "merge";
    
    // ------------------------------------------------------------------------

    public static final FxMergeFunc INSTANCE = new FxMergeFunc();
    
    private FxMergeFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public FxNode eval(FxChildAdder dest, FxNode src) {
        // TODO Auto-generated method stub
        return null;
    }


    
}