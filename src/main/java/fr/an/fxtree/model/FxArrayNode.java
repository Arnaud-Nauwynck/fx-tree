package fr.an.fxtree.model;

import java.util.Collection;

public abstract class FxArrayNode extends FXContainerNode {

    // ------------------------------------------------------------------------
    
    protected FxArrayNode(FXContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitArray(this);
    }

    @Override
    public <P,R> R accept(FxTreeVisitor2<P,R> visitor, P param) {
        return visitor.visitArray(this, param);
    }

    @Override
    public abstract int size();
    
    public abstract FxNode get(int index);

    public abstract Collection<FxNode> children();

    public abstract <T extends FxNode> T insert(int index, Class<T> clss);

    public <T extends FxNode> T add(Class<T> clss) {
        int len = size();
        return insert(len, clss);
    }

    @Override
    public abstract void remove(FxNode child);

    @Override
    public abstract FxNode remove(FxChildId childId);
    
    public abstract FxNode remove(int index);

    // helper methods for insert(int index, Class<T> clss) or add(Class<T> clss)
    // ------------------------------------------------------------------------

    public FxArrayNode insertArray(int index) {
        return insert(index, FxArrayNode.class);
    }

    public FxArrayNode addArray() {
        return insertArray(size());
    }

    public FxObjNode insertObj(int index) {
        return insert(index, FxObjNode.class);
    }

    public FxObjNode addObj() {
        return insertObj(size());
    }

    public FxTextNode insert(int index, String value) {
        FxTextNode res = insert(index, FxTextNode.class);
        res.setValue(value);
        return res;
    }

    public FxTextNode add(String value) {
        return insert(size(), value);
    }

    public FxDoubleNode insert(int index, double value) {
        FxDoubleNode res = insert(index, FxDoubleNode.class);
        res.setValue(value);
        return res;
    }
    
    public FxDoubleNode add(double value) {
        return insert(size(), value);
    }
    
    public FxIntNode insert(int index, int value) {
        FxIntNode res = insert(index, FxIntNode.class);
        res.setValue(value);
        return res;
    }

    public FxIntNode add(int value) {
        return insert(size(), value);
    }

    public FxBoolNode insert(int index, boolean value) {
        FxBoolNode res = insert(index, FxBoolNode.class);
        res.setValue(value);
        return res; 
    }

    public FxBoolNode add(boolean value) {
        return insert(size(), value); 
    }
    
    public FxPOJONode insertPOJO(int index, Object value) {
        FxPOJONode res = insert(index, FxPOJONode.class);
        res.setValue(value);
        return res;
    }

    public FxPOJONode addPOJO(Object value) {
        return insertPOJO(size(), value);
    }

    public FxNullNode insertNull(int index) {
        return insert(index, FxNullNode.class);
    }
    
    public FxNullNode addNull() {
        return insertNull(size());
    }
    
}
