package fr.an.fxtree.model;

import java.util.Arrays;

import com.fasterxml.jackson.core.Base64Variants;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;

public abstract class FxBinaryNode extends FxValueNode {

    // ------------------------------------------------------------------------

    protected FxBinaryNode(FxContainerNode parent, FxChildId childId, FxSourceLoc sourceLoc) {
        super(parent, childId, sourceLoc);
    }

    // ------------------------------------------------------------------------

    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.BINARY;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitBinaryValue(this);
    }

    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitBinaryValue(this, param);
    }

    public abstract byte[] getValue();

    public abstract void setValue(byte[] value);

    public byte[] getValueClone() {
        byte[] tmpres = getValue();
        if (tmpres != null) {
            tmpres = tmpres.clone();
        }
        return tmpres;
    }

    @Override
    public byte[] binaryValue() {
        return getValue();
    }

    @Override
    public String asText() {
        return Base64Variants.getDefaultVariant().encode(getValue(), false);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (!(o instanceof FxBinaryNode)) {
            return false;
        }
        return Arrays.equals(((FxBinaryNode) o).getValue(), getValue());
    }

    @Override
    public abstract int hashCode();

    @Override
    public String toString() {
        return Base64Variants.getDefaultVariant().encode(getValue(), false);
    }

}
