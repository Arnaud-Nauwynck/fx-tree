package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxDoubleNode;

public class FxMemDoubleNode extends FxDoubleNode {

    private double value;

    // ------------------------------------------------------------------------

    protected FxMemDoubleNode(FxContainerNode parent, FxMemChildId childId, double value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------

    @Override
	public double getValue() {
        return value;
    }

    @Override
	public void setValue(double value) {
        this.value = value;
    }


}
