package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxObjNode;

public class FxMemNodeFactory2 extends FxMemNodeFactory {

    public static FxMemNodeFactory2 DEFAULT = new FxMemNodeFactory2();
    
    // ------------------------------------------------------------------------

    public FxMemNodeFactory2() {
    }

    // ------------------------------------------------------------------------

    @Override
    public FxObjNode newObj() {
        return new FxMemObjNode2(null, null); 
    }
    
}
