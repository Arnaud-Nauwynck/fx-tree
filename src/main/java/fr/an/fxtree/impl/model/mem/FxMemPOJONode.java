package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxPOJONode;

public class FxMemPOJONode extends FxPOJONode {

    private Object value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemPOJONode(FxContainerNode parent, FxMemChildId childId, FxSourceLoc sourceLoc, Object value) {
        super(parent, childId, sourceLoc);
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
}
