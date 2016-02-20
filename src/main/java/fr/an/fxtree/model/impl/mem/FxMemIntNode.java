package fr.an.fxtree.model.impl.mem;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxIntNode;

public class FxMemIntNode extends FxIntNode {

    private int value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemIntNode(FXContainerNode parent, FxMemChildId childId, int value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return Integer.toString(value);
    }
    
}
