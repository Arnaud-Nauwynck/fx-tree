package fr.an.fxtree.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.io.NumberOutput;

public abstract class FxIntNode extends FxValueNode {

    // ------------------------------------------------------------------------

    protected FxIntNode(FxContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------

    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.NUMBER;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitIntValue(this);
    }

    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitIntValue(this, param);
    }

    public abstract int getValue();

    public abstract void setValue(int value);

    @Override
    public FxNumberType numberType() {
        return FxNumberType.INT;
    }

    @Override
    public boolean isIntegralNumber() {
        return true;
    }

    @Override
    public boolean isInt() {
        return true;
    }

    @Override
    public boolean canConvertToInt() {
        return true;
    }

    @Override
    public boolean canConvertToLong() {
        return true;
    }

    @Override
    public Number numberValue() {
        return Integer.valueOf(getValue());
    }

    @Override
    public short shortValue() {
        return (short) getValue();
    }

    @Override
    public int intValue() {
        return getValue();
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

    @Override
	public final int asInt() {
        return intValue();
    }

    @Override
    public final int asInt(int defaultValue) {
        return intValue();
    }


    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return Integer.toString(getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof FxIntNode) {
            return ((FxIntNode) o).getValue() == getValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getValue();
    }

}
