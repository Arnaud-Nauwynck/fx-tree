package fr.an.fxtree.model.impl.mem;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxBoolNode;

public class FxMemBoolNode extends FxBoolNode {

    private boolean value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemBoolNode(FXContainerNode parent, FxMemChildId childId, boolean value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public boolean getValue() {
        return value;
    }

    @Override
    public void setValue(boolean value) {
        this.value = value;
    }

    // ------------------------------------------------------------------------
        
}
