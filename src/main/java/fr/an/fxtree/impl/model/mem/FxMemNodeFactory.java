package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxBoolNode;
import fr.an.fxtree.model.FxDoubleNode;
import fr.an.fxtree.model.FxIntNode;
import fr.an.fxtree.model.FxLinkProxyNode;
import fr.an.fxtree.model.FxLongNode;
import fr.an.fxtree.model.FxNodeFactoryRegistry;
import fr.an.fxtree.model.FxNullNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.FxTextNode;
import fr.an.fxtree.model.path.FxNodeOuterPath;

public class FxMemNodeFactory extends FxNodeFactoryRegistry {

    public static FxMemNodeFactory DEFAULT = new FxMemNodeFactory();
    
    // ------------------------------------------------------------------------

    public FxMemNodeFactory() {
    }

    // ------------------------------------------------------------------------

    @Override
    public FxArrayNode newArray(FxSourceLoc loc) {
        return new FxMemArrayNode(null, null, loc);
    }

    @Override
    public FxObjNode newObj(FxSourceLoc loc) {
        return new FxMemObjNode(null, null, loc); 
    }
    
    @Override
    public FxTextNode newText(FxSourceLoc loc) {
        return new FxMemTextNode(null, null, loc, null);
    }
    
    @Override
    public FxDoubleNode newDouble(FxSourceLoc loc) {
        return new FxMemDoubleNode(null, null, loc, 0.0);
    }
    
    @Override
    public FxIntNode newInt(FxSourceLoc loc) {
        return new FxMemIntNode(null, null, loc, 0);
    }

    @Override
    public FxLongNode newLong(FxSourceLoc loc) {
        return new FxMemLongNode(null, null, loc, 0L);
    }

    @Override
    public FxBoolNode newBool(FxSourceLoc loc) {
        return new FxMemBoolNode(null, null, loc, false);
    }

    @Override
    public FxBinaryNode newBinary(FxSourceLoc loc) {
        return new FxMemBinaryNode(null, null, loc, null);
    }

    @Override
    public FxPOJONode newPOJO(FxSourceLoc loc) {
        return new FxMemPOJONode(null, null, loc, null);
    }

    @Override
    public FxLinkProxyNode newLink(FxSourceLoc loc) {
        return new FxMemLinkProxyNode(null, null, loc);
    }

    @Override
    public FxLinkProxyNode newLink(FxNodeOuterPath path, FxSourceLoc loc) {
        return new FxMemLinkProxyNode(null, null, loc, path);
    }

    @Override
    public FxNullNode newNull(FxSourceLoc loc) {
        return new FxMemNullNode(null, null, loc);
    }
    
}
