package fr.an.fxtree.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.io.NumberOutput;

public abstract class FxLongNode extends FxValueNode {

    // ------------------------------------------------------------------------

    protected FxLongNode(FxContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------

    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.NUMBER;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitLongValue(this);
    }

    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitLongValue(this, param);
    }

    public abstract long getValue();

    public abstract void setValue(long value);

    @Override
    public FxNumberType numberType() {
        return FxNumberType.LONG;
    }

    @Override
    public boolean isIntegralNumber() {
        return true;
    }

    @Override
    public boolean isLong() {
        return true;
    }

    @Override
    public boolean canConvertToInt() {
        long _value = getValue();
        return (_value >= Integer.MIN_VALUE && _value <= Integer.MAX_VALUE);
    }

    @Override
    public boolean canConvertToLong() {
        return true;
    }

    @Override
    public Number numberValue() {
        return Long.valueOf(getValue());
    }

    @Override
    public short shortValue() {
        return (short) getValue();
    }

    @Override
    public int intValue() {
        return (int) getValue();
    }

    @Override
    public long longValue() {
        return getValue();
    }

    @Override
    public float floatValue() {
        return getValue();
    }

    @Override
    public double doubleValue() {
        return getValue();
    }

    @Override
    public BigDecimal decimalValue() {
        return BigDecimal.valueOf(getValue());
    }

    @Override
    public BigInteger bigIntegerValue() {
        return BigInteger.valueOf(getValue());
    }

    @Override
    public String asText() {
        return NumberOutput.toString(getValue());
    }

    @Override
    public boolean asBoolean(boolean defaultValue) {
        return getValue() != 0;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (o instanceof FxLongNode) {
            return ((FxLongNode) o).getValue() == getValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getValue());
    }

    @Override
    public String toString() {
        return Long.toString(getValue());
    }

}
