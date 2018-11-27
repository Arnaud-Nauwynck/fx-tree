package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxRootDocument;

public class FxMemRootDocument extends FxRootDocument {
    
    // ------------------------------------------------------------------------
    
//    public FxMemRootDocument() {
//        this(FxMemNodeFactory.DEFAULT, DEFAULT_SOURCE);
//    }
    
    public FxMemRootDocument(FxSourceLoc sourceLoc) {
        this(FxMemNodeFactory.DEFAULT, sourceLoc);
    }
    
    public FxMemRootDocument(FxMemNodeFactory nodeFactory, FxSourceLoc sourceLoc) {
        super(nodeFactory, sourceLoc);
    }

    public static FxMemRootDocument newInMem() {
        return new FxMemRootDocument(FxSourceLoc.inMem());
    }
    
    public static FxChildWriter inMemWriter() {
        return newInMem().contentWriter();
    }
    
    // ------------------------------------------------------------------------


}
