package fr.an.fxtree.model;

import java.io.IOException;

public abstract class FxPOJONode extends FxValueNode {

    // ------------------------------------------------------------------------

    protected FxPOJONode(FxContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------

    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.POJO;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitPOJOValue(this);
    }

    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitPOJOValue(this, param);
    }

    public abstract Object getValue();

    public abstract void setValue(Object value);

    @Override
    public byte[] binaryValue() throws IOException {
        Object value = getValue();
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        return super.binaryValue();
    }

    // General type coercions
    // ------------------------------------------------------------------------

    @Override
    public String asText() {
        Object value = getValue();
        return (value == null) ? "null" : value.toString();
    }

    @Override
    public String asText(String defaultValue) {
        Object value = getValue();
        return (value == null) ? defaultValue : value.toString();
    }

    @Override
    public boolean asBoolean(boolean defaultValue) {
        Object value = getValue();
        if (value != null && value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        return defaultValue;
    }

    @Override
    public int asInt(int defaultValue) {
        Object value = getValue();
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    @Override
    public long asLong(long defaultValue) {
        Object value = getValue();
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }

    @Override
    public double asDouble(double defaultValue) {
        Object value = getValue();
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        Object value = getValue();
        return (value != null) ? value.toString() : "null";
    }

}
