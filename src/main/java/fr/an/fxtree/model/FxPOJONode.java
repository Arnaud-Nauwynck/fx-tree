package fr.an.fxtree.model;

import java.text.SimpleDateFormat;

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
    public byte[] binaryValue() {
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
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof FxPOJONode) {
            return _pojoEquals((FxPOJONode) o);
        }
        return false;
    }

    protected boolean _pojoEquals(FxPOJONode other) {
        Object value = getValue();
        Object otherValue = other.getValue();
        return ((value == otherValue)
                || (value != null && otherValue != null && value.equals(otherValue)));
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public String toString() {
        Object value = getValue();
        if (value instanceof java.util.Date) {
            java.util.Date d = (java.util.Date) value;
            return "\"" + d.getTime() + " (=" + new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(d) + ")\"";
        }
        return (value != null) ? value.toString() : "null";
    }

}
