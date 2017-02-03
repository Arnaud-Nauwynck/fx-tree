package fr.an.fxtree.model.reader;

import java.math.BigDecimal;
import java.math.BigInteger;

import fr.an.fxtree.model.FxNodeType;
import fr.an.fxtree.model.FxNumberType;

public interface IFxNodeReader {

//    public abstract void accept(FxTreeVisitor visitor);
//
//    public abstract <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param);

    // hidden/denied access to parent ... getParent() getChildId() */

    public abstract FxNodeType getNodeType();

    public boolean isValueNode();

    public boolean isContainerNode();

    public boolean isMissingNode();

    public boolean isArray();

    public boolean isObject();

    public boolean isPojo();

    public boolean isNumber();

    public FxNumberType numberType();

    public boolean isIntegralNumber();

    public boolean isFloatingPointNumber();

    public boolean isShort();

    public boolean isInt();

    public boolean isLong();

    public boolean isFloat();

    public boolean isDouble();

    public boolean isBigDecimal();

    public boolean isBigInteger();

    public boolean isTextual();

    public boolean isBoolean();

    public boolean isNull();

    public boolean isBinary();

    public boolean canConvertToInt();

    public boolean canConvertToLong();

    // Public API, straight value access
    // ------------------------------------------------------------------------

    public String textValue();

    public byte[] binaryValue();

    public boolean booleanValue();

    public Number numberValue();

    public short shortValue();

    public int intValue();

    public long longValue();

    public float floatValue();

    public double doubleValue();

    public BigDecimal decimalValue();

    public BigInteger bigIntegerValue();

    // Public API, value access with conversion(s)/coercion(s)
    // ------------------------------------------------------------------------

    public String asText();

    public String asText(String defaultValue);

    public int asInt();

    public int asInt(int defaultValue);

    public long asLong();

    public long asLong(long defaultValue);

    public double asDouble();

    public double asDouble(double defaultValue);

    public boolean asBoolean();

    public boolean asBoolean(boolean defaultValue);

}
