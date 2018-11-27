package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxDoubleNode;

public class FxMemDoubleNode extends FxDoubleNode {

    private double value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemDoubleNode(FxContainerNode parent, FxMemChildId childId, FxSourceLoc sourceLoc, double value) {
        super(parent, childId, sourceLoc);
        this.value = value;
    }

    // ------------------------------------------------------------------------
        
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }


}
