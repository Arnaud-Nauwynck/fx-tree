package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxPOJONode;

public class FxMemPOJONode extends FxPOJONode {

    private Object value;

    // ------------------------------------------------------------------------

    protected FxMemPOJONode(FxContainerNode parent, FxMemChildId childId, Object value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------

    @Override
	public Object getValue() {
        return value;
    }

    @Override
	public void setValue(Object value) {
        this.value = value;
    }

    // ------------------------------------------------------------------------

}
