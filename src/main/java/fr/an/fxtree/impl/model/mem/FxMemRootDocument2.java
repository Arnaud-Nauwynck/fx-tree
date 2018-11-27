package fr.an.fxtree.impl.model.mem;

public class FxMemRootDocument2 extends FxMemRootDocument {

    // ------------------------------------------------------------------------

//    public FxMemRootDocument2() {
//        this(FxMemNodeFactory2.DEFAULT, FxSourceLoc.inMem());
//    }

    public FxMemRootDocument2(FxSourceLoc sourceLoc) {
        this(FxMemNodeFactory2.DEFAULT, sourceLoc);
    }
    
    public FxMemRootDocument2(FxMemNodeFactory2 nodeFactory, FxSourceLoc sourceLoc) {
        super(nodeFactory, sourceLoc);
    }

    // ------------------------------------------------------------------------


}
