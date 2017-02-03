package fr.an.fxtree.model;

public abstract class FxBoolNode extends FxValueNode {

    // ------------------------------------------------------------------------

    protected FxBoolNode(FxContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------

    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.BOOLEAN;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitBoolValue(this);
    }

    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitBoolValue(this, param);
    }

    public abstract boolean getValue();

    public abstract void setValue(boolean value);


    @Override
    public boolean booleanValue() {
        return getValue();
    }

    @Override
    public String asText() {
        return getValue() ? "true" : "false";
    }

    @Override
    public boolean asBoolean() {
        return getValue();
    }

    @Override
    public boolean asBoolean(boolean defaultValue) {
        return getValue();
    }

    @Override
    public int asInt(int defaultValue) {
        return getValue() ? 1 : 0;
    }
    @Override
    public long asLong(long defaultValue) {
        return getValue() ? 1L : 0L;
    }
    @Override
    public double asDouble(double defaultValue) {
        return getValue() ? 1.0 : 0.0;
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return getValue() ? 3 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof FxBoolNode)) {
            return false;
        }
        return (getValue() == ((FxBoolNode) o).getValue());
    }

    @Override
    public String toString() {
        return Boolean.toString(getValue());
    }

}
