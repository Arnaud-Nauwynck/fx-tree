package fr.an.fxtree.model.func;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

public abstract class FxNodeFunc {

    public abstract void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src);
    
    protected FxMemRootDocument newTmpDoc(FxNode src) {
        return new FxMemRootDocument(src.getSourceLoc());
    }
}
