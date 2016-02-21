package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxNullNode;

public class FxMemNullNode extends FxNullNode {
    
    // ------------------------------------------------------------------------
    
    protected FxMemNullNode(FxContainerNode parent, FxMemChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "null";
    }
}
