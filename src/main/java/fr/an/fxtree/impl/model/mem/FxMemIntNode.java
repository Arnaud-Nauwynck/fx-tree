package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxIntNode;

public class FxMemIntNode extends FxIntNode {

    private int value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemIntNode(FxContainerNode parent, FxMemChildId childId, FxSourceLoc sourceLoc, int value) {
        super(parent, childId, sourceLoc);
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
