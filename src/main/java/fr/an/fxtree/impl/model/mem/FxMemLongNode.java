package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxLongNode;

public class FxMemLongNode extends FxLongNode {

    private long value;

    // ------------------------------------------------------------------------

    protected FxMemLongNode(FxContainerNode parent, FxMemChildId childId, long value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------

    @Override
	public long getValue() {
        return value;
    }

    @Override
	public void setValue(long value) {
        this.value = value;
    }

    // ------------------------------------------------------------------------

}
