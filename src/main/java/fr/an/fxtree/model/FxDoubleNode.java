package fr.an.fxtree.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.io.NumberOutput;

public abstract class FxDoubleNode extends FxValueNode {

    // ------------------------------------------------------------------------

    protected FxDoubleNode(FxContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------

    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.NUMBER;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitDoubleValue(this);
    }

    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitDoubleValue(this, param);
    }

    public abstract double getValue();

    public abstract void setValue(double value);

    @Override
    public FxNumberType numberType() { return FxNumberType.DOUBLE; }


    @Override
    public boolean isFloatingPointNumber() { return true; }

    @Override
    public boolean isDouble() { return true; }

    @Override public boolean canConvertToInt() {
        return (getValue() >= Integer.MIN_VALUE && getValue() <= Integer.MAX_VALUE);
    }
    @Override public boolean canConvertToLong() {
        return (getValue() >= Long.MIN_VALUE && getValue() <= Long.MAX_VALUE);
    }

    @Override
    public Number numberValue() {
        return Double.valueOf(getValue());
    }

    @Override
    public short shortValue() { return (short) getValue(); }

    @Override
    public int intValue() { return (int) getValue(); }

    @Override
    public long longValue() { return (long) getValue(); }

    @Override
    public float floatValue() { return (float) getValue(); }

    @Override
    public double doubleValue() { return getValue(); }

    @Override
    public BigDecimal decimalValue() { return BigDecimal.valueOf(getValue()); }

    @Override
    public BigInteger bigIntegerValue() {
        return decimalValue().toBigInteger();
    }

    @Override
    public String asText() {
        return NumberOutput.toString(getValue());
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
        if (o instanceof FxDoubleNode) {
            final double otherValue = ((FxDoubleNode) o).getValue();
            // .. NaN does not equal NaN
            return Double.compare(getValue(), otherValue) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(getValue());
    }

    @Override
    public String toString() {
        return Double.toString(getValue());
    }

}
