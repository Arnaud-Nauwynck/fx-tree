package fr.an.fxtree.model.func;

import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

public abstract class FxBindedNodeFuncExpr {

    private FxNode src;

    // ------------------------------------------------------------------------

    public FxBindedNodeFuncExpr() {
    }

    // ------------------------------------------------------------------------

    public abstract FxNode eval(FxChildWriter dest);



    public FxNode getSrc() {
        return src;
    }

    public void setSrc(FxNode src) {
        this.src = src;
    }


}
