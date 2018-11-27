package fr.an.fxtree.model;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.path.FxNodeOuterPath;

public abstract class FxNodeFactoryRegistry {
    
    public abstract FxArrayNode newArray(FxSourceLoc loc);
    
    public abstract FxObjNode newObj(FxSourceLoc loc);
    
    public abstract FxTextNode newText(FxSourceLoc loc);

    public FxTextNode newText(String value, FxSourceLoc loc) {
        FxTextNode res = newText(loc);
        res.setValue(value);
        return res;
    }

    public abstract FxDoubleNode newDouble(FxSourceLoc loc);
    
    public FxDoubleNode newDouble(double value, FxSourceLoc loc) {
        FxDoubleNode res = newDouble(loc);
        res.setValue(value);
        return res;
    }
    
    public abstract FxIntNode newInt(FxSourceLoc loc);

    public FxIntNode newInt(int value, FxSourceLoc loc) {
        FxIntNode res = newInt(loc);
        res.setValue(value);
        return res;
    }

    public abstract FxLongNode newLong(FxSourceLoc loc);

    public FxLongNode newLong(long value, FxSourceLoc loc) {
        FxLongNode res = newLong(loc);
        res.setValue(value);
        return res;
    }

    public abstract FxBoolNode newBool(FxSourceLoc loc);

    public FxBoolNode newBool(boolean value, FxSourceLoc loc) {
        FxBoolNode res = newBool(loc);
        res.setValue(value);
        return res; 
    }

    public abstract FxBinaryNode newBinary(FxSourceLoc loc);

    public FxBinaryNode newBinary(byte[] value, FxSourceLoc loc) {
        FxBinaryNode res = newBinary(loc);
        res.setValue(value);
        return res; 
    }

    public abstract FxPOJONode newPOJO(FxSourceLoc loc);

    public FxPOJONode newPOJO(Object value, FxSourceLoc loc) {
        FxPOJONode res = newPOJO(loc);
        res.setValue(value);
        return res;
    }

    // TODO deprecated
    public abstract FxLinkProxyNode newLink(FxSourceLoc loc);

    public FxLinkProxyNode newLink(FxNodeOuterPath value, FxSourceLoc loc) {
        FxLinkProxyNode res = newLink(loc);
        res.setTargetRelativePath(value);
        return res;
    }
    
    public abstract FxNullNode newNull(FxSourceLoc loc);
 
}
