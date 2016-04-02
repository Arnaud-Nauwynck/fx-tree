package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxBoolNode;
import fr.an.fxtree.model.FxDoubleNode;
import fr.an.fxtree.model.FxIntNode;
import fr.an.fxtree.model.FxLinkProxyNode;
import fr.an.fxtree.model.FxLongNode;
import fr.an.fxtree.model.FxNode;
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

    public <T extends FxNode> T newNode(Class<T> clss) {
        try {
            return clss.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to create instance for " + clss, ex);
        }
    }
    
    @Override
    public FxArrayNode newArray() {
        return new FxMemArrayNode(null, null);
    }

    @Override
    public FxObjNode newObj() {
        return new FxMemObjNode(null, null); 
    }
    
    @Override
    public FxTextNode newText() {
        return new FxMemTextNode(null, null, null);
    }
    
    @Override
    public FxDoubleNode newDouble() {
        return new FxMemDoubleNode(null, null, 0.0);
    }
    
    @Override
    public FxIntNode newInt() {
        return new FxMemIntNode(null, null, 0);
    }

    @Override
    public FxLongNode newLong() {
        return new FxMemLongNode(null, null, 0L);
    }

    @Override
    public FxBoolNode newBool() {
        return new FxMemBoolNode(null, null, false);
    }

    @Override
    public FxBinaryNode newBinary() {
        return new FxMemBinaryNode(null, null, null);
    }

    @Override
    public FxPOJONode newPOJO() {
        return new FxMemPOJONode(null, null, null);
    }

    @Override
    public FxLinkProxyNode newLink() {
        return new FxMemLinkProxyNode(null, null);
    }

    @Override
    public FxLinkProxyNode newLink(FxNodeOuterPath path) {
        return new FxMemLinkProxyNode(null, null, path);
    }

    @Override
    public FxNullNode newNull() {
        return new FxMemNullNode(null, null);
    }
}
