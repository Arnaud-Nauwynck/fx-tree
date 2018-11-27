package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxNullNode;

public class FxMemNullNode extends FxNullNode {
    
    // ------------------------------------------------------------------------
    
    protected FxMemNullNode(FxContainerNode parent, FxMemChildId childId, FxSourceLoc sourceLoc) {
        super(parent, childId, sourceLoc);
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "null";
    }
}
