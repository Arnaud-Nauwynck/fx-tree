package fr.an.fxtree.model.impl.mem;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBoolNode;
import fr.an.fxtree.model.FxDoubleNode;
import fr.an.fxtree.model.FxIntNode;
import fr.an.fxtree.model.FxNodeFactory;
import fr.an.fxtree.model.FxNullNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.FxTextNode;

public abstract class FxMemNodeFactory extends FxNodeFactory {

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
    public FxBoolNode newBool() {
        return new FxMemBoolNode(null, null, false);
    }
    
    @Override
    public FxPOJONode newPOJO() {
        return new FxMemPOJONode(null, null, null);
    }

    @Override
    public FxNullNode newNull() {
        return new FxMemNullNode(null, null);
    }
}
