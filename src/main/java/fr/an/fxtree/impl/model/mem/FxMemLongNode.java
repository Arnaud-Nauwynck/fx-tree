package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxLongNode;

public class FxMemLongNode extends FxLongNode {

    private long value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemLongNode(FxContainerNode parent, FxMemChildId childId, FxSourceLoc sourceLoc, long value) {
        super(parent, childId, sourceLoc);
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    // ------------------------------------------------------------------------
        
}
