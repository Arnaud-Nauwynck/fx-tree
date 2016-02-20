package fr.an.fxtree.model.impl.mem;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxTextNode;

public class FxMemTextNode extends FxTextNode {

    private String value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemTextNode(FXContainerNode parent, FxMemChildId childId, String value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------
        
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return value;
    }
    
}
