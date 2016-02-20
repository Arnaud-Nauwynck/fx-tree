package fr.an.fxtree.model.impl.mem;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxDoubleNode;

public class FxMemDoubleNode extends FxDoubleNode {

    private double value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemDoubleNode(FXContainerNode parent, FxMemChildId childId, double value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------
        
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return Double.toString(value);
    }
}
