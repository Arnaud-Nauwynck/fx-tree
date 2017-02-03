package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxTextNode;

public class FxMemTextNode extends FxTextNode {

    private String value;

    // ------------------------------------------------------------------------

    protected FxMemTextNode(FxContainerNode parent, FxMemChildId childId, String value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------

    @Override
	public String getValue() {
        return value;
    }

    @Override
	public void setValue(String value) {
        this.value = value;
    }

}
