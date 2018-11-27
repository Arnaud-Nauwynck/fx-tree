package fr.an.fxtree.model;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;

public abstract class FxValueNode extends FxNode {

    // ------------------------------------------------------------------------
    
    protected FxValueNode(FxContainerNode parent, FxChildId childId, FxSourceLoc sourceLoc) {
        super(parent, childId, sourceLoc);
    }

    // ------------------------------------------------------------------------
    
}
