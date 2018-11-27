package fr.an.fxtree.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.path.FxNodeOuterPath;

public abstract class FxChildWriter {

    protected abstract FxSourceLoc insertLoc();
    
    public abstract void remove();
    public abstract FxNode getResultChild();
    
    public final boolean canAddMoveFrom(FxRootDocument otherParentSrc) { return false; }
    public final FxNode addMoveFrom(FxRootDocument otherParentSrc) { return null; }
    
    // public FxChildId getChildId(); ??
    
    public abstract FxArrayNode addArray(FxSourceLoc loc);

    public abstract FxObjNode addObj(FxSourceLoc loc);

    public abstract FxTextNode add(String value, FxSourceLoc loc);

    public abstract FxDoubleNode add(double value, FxSourceLoc loc);

    public abstract FxIntNode add(int value, FxSourceLoc loc);

    public abstract FxLongNode add(long value, FxSourceLoc loc);

    public abstract FxBoolNode add(boolean value, FxSourceLoc loc);

    public abstract FxBinaryNode add(byte[] value, FxSourceLoc loc);

    public abstract FxPOJONode add(BigInteger value, FxSourceLoc loc);

    public abstract FxPOJONode add(BigDecimal value, FxSourceLoc loc);

    public abstract FxPOJONode addPOJO(Object value, FxSourceLoc loc);

    public abstract FxLinkProxyNode addLink(FxNodeOuterPath value, FxSourceLoc loc);

    public abstract FxNullNode addNull(FxSourceLoc loc);

}
