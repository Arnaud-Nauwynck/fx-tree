package fr.an.fxtree.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class FxNode {

    private FxContainerNode parent;
    private FxChildId childId;

    // ------------------------------------------------------------------------

    protected FxNode(FxContainerNode parent, FxChildId childId) {
        this.parent = parent;
        this.childId = childId;
    }

    // ------------------------------------------------------------------------

    public abstract void accept(FxTreeVisitor visitor);

    public abstract <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param);

    public FxContainerNode getParent() {
        return parent;
    }

    public FxChildId getChildId() {
        return childId;
    }

    /* protected */ public void _setParent(FxContainerNode parent, FxChildId childId) {
        this.parent = parent;
        this.childId = childId;
    }

    public abstract FxNodeType getNodeType();

    public final boolean isValueNode() {
        switch (getNodeType()) {
        case ARRAY:
        case OBJECT:
        case MISSING:
            return false;
        default:
            return true;
        }
    }

    public final boolean isContainerNode() {
        final FxNodeType type = getNodeType();
        return type == FxNodeType.OBJECT || type == FxNodeType.ARRAY;
    }

    public final boolean isMissingNode() {
        return getNodeType() == FxNodeType.MISSING;
    }

    public final boolean isArray() {
        return getNodeType() == FxNodeType.ARRAY;
    }

    public final boolean isObject() {
        return getNodeType() == FxNodeType.OBJECT;
    }

    public final boolean isPojo() {
        return getNodeType() == FxNodeType.POJO;
    }

    public final boolean isNumber() {
        return getNodeType() == FxNodeType.NUMBER;
    }

    public FxNumberType numberType() {
        return null;
    }

    public boolean isIntegralNumber() {
        return false;
    }

    public boolean isFloatingPointNumber() {
        return false;
    }

    public boolean isShort() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isLong() {
        return false;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean isDouble() {
        return false;
    }

    public boolean isBigDecimal() {
        return false;
    }

    public boolean isBigInteger() {
        return false;
    }

    public final boolean isTextual() {
        return getNodeType() == FxNodeType.STRING;
    }

    public final boolean isBoolean() {
        return getNodeType() == FxNodeType.BOOLEAN;
    }

    public final boolean isNull() {
        return getNodeType() == FxNodeType.NULL;
    }

    public final boolean isBinary() {
        return getNodeType() == FxNodeType.BINARY;
    }

    public boolean canConvertToInt() {
        return false;
    }

    public boolean canConvertToLong() {
        return false;
    }

    // Public API, straight value access
    // ------------------------------------------------------------------------

    public String textValue() {
        return null;
    }

    public byte[] binaryValue() throws IOException {
        return null;
    }

    public boolean booleanValue() {
        return false;
    }

    public Number numberValue() {
        return null;
    }

    public short shortValue() {
        return 0;
    }

    public int intValue() {
        return 0;
    }

    public long longValue() {
        return 0L;
    }

    public float floatValue() {
        return 0.0f;
    }

    public double doubleValue() {
        return 0.0;
    }

    public BigDecimal decimalValue() {
        return BigDecimal.ZERO;
    }

    public BigInteger bigIntegerValue() {
        return BigInteger.ZERO;
    }

    // Public API, value access with conversion(s)/coercion(s)
    // ------------------------------------------------------------------------

    public abstract String asText();

    public String asText(String defaultValue) {
        String str = asText();
        return (str == null) ? defaultValue : str;
    }

    public int asInt() {
        return asInt(0);
    }

    public int asInt(int defaultValue) {
        return defaultValue;
    }

    public long asLong() {
        return asLong(0L);
    }

    public long asLong(long defaultValue) {
        return defaultValue;
    }

    public double asDouble() {
        return asDouble(0.0);
    }

    public double asDouble(double defaultValue) {
        return defaultValue;
    }

    public boolean asBoolean() {
        return asBoolean(false);
    }

    public boolean asBoolean(boolean defaultValue) {
        return defaultValue;
    }

}
