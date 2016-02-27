package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxFixedCompoundFunc extends FxNodeFunc {

    private final FxNodeFunc first;
    private final FxNodeFunc then;
    
    // ------------------------------------------------------------------------

    public FxFixedCompoundFunc(FxNodeFunc first, FxNodeFunc then) {
        this.first = first;
        this.then = then;
    }


    public static FxNodeFunc chainIfNotNull(FxNodeFunc first, FxNodeFunc optionalThen) {
        return (optionalThen != null)? new FxFixedCompoundFunc(first, optionalThen) : first;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public FxNode eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxMemRootDocument tmpDoc = new FxMemRootDocument();
        FxChildWriter tmpAdder = tmpDoc.contentWriter();
        
        FxNode tmpres = first.eval(tmpAdder, ctx, src);
        if (tmpres == null) {
            return null; // useless? "then" function should accept "null"
        }
        
        FxNode res = then.eval(dest, ctx, tmpres);

        return res;
    }
    
    
}
