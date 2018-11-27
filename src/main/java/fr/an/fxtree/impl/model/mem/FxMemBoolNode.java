package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxBoolNode;
import fr.an.fxtree.model.FxContainerNode;

public class FxMemBoolNode extends FxBoolNode {

    private boolean value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemBoolNode(FxContainerNode parent, FxMemChildId childId, FxSourceLoc sourceLoc, boolean value) {
        super(parent, childId, sourceLoc);
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

}
