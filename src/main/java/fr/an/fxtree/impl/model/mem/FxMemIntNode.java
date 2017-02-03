package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxIntNode;

public class FxMemIntNode extends FxIntNode {

    private int value;

    // ------------------------------------------------------------------------

    protected FxMemIntNode(FxContainerNode parent, FxMemChildId childId, int value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------

    @Override
	public int getValue() {
        return value;
    }

    @Override
	public void setValue(int value) {
        this.value = value;
    }

}
