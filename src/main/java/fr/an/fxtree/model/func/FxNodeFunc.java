package fr.an.fxtree.model.func;

import fr.an.fxtree.model.FxChildAdder;
import fr.an.fxtree.model.FxNode;

public abstract class FxNodeFunc {

    public abstract FxNode eval(FxChildAdder dest, FxEvalContext ctx, FxNode src);
    
}
