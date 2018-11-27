package fr.an.fxtree.impl.model.mem;

import java.util.Arrays;

import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxContainerNode;

public class FxMemBinaryNode extends FxBinaryNode {

    private byte[] value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemBinaryNode(FxContainerNode parent, FxMemChildId childId, FxSourceLoc sourceLoc, byte[] value) {
        super(parent, childId, sourceLoc);
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
    
}
