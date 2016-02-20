package fr.an.fxtree.model.impl.mem;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxNullNode;

public class FxMemNullNode extends FxNullNode {
    
    // ------------------------------------------------------------------------
    
    protected FxMemNullNode(FXContainerNode parent, FxMemChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "null";
    }
}
