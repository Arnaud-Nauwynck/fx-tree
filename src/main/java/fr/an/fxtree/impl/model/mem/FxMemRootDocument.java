package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxRootDocument;

public class FxMemRootDocument extends FxRootDocument {

    // ------------------------------------------------------------------------

    public FxMemRootDocument() {
        this(FxMemNodeFactory.DEFAULT);
    }

    public FxMemRootDocument(FxMemNodeFactory nodeFactory) {
        super(nodeFactory);
    }

    // ------------------------------------------------------------------------


}
