package fr.an.fxtree.impl.model.mem;

import java.util.Arrays;

import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxContainerNode;

public class FxMemBinaryNode extends FxBinaryNode {

    private byte[] value;

    // ------------------------------------------------------------------------

    protected FxMemBinaryNode(FxContainerNode parent, FxMemChildId childId, byte[] value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------

    @Override
	public byte[] getValue() {
        return value;
    }

    @Override
	public void setValue(byte[] value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

}
