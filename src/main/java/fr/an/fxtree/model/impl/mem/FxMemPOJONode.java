package fr.an.fxtree.model.impl.mem;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxPOJONode;

public class FxMemPOJONode extends FxPOJONode {

    private Object value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemPOJONode(FXContainerNode parent, FxMemChildId childId, Object value) {
        super(parent, childId);
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
    
    @Override
    public String toString() {
        return (value != null)? value.toString() : "null";
    }
    
}
