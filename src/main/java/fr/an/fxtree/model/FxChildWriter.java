package fr.an.fxtree.model;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class FxChildWriter {

    public abstract void remove();
    public abstract FxNode getResultChild();
    
    public abstract boolean canAddMoveFrom(FxRootDocument otherParentSrc);
    public abstract FxNode addMoveFrom(FxRootDocument otherParentSrc);
    
    // public FxChildId getChildId(); ??
    
    public abstract FxArrayNode addArray();

    public abstract FxObjNode addObj();

    // public abstract FxTextNode addText();
    public abstract FxTextNode add(String value);

    // public abstract FxDoubleNode addDouble();
    public abstract FxDoubleNode add(double value);

    // public abstract FxIntNode addInt();
    public abstract FxIntNode add(int value);

    public abstract FxLongNode add(long value);

    public abstract FxBoolNode add(boolean value);

    public abstract FxBinaryNode add(byte[] value);

    public abstract FxPOJONode add(BigInteger value);

    public abstract FxPOJONode add(BigDecimal value);

    public abstract FxPOJONode addPOJO(Object value);

    public abstract FxNullNode addNull();
    
}
