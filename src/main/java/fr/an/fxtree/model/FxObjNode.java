package fr.an.fxtree.model;

import java.util.Collection;

public abstract class FxObjNode extends FXContainerNode {

    // ------------------------------------------------------------------------
    
    protected FxObjNode(FXContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.OBJECT;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitObj(this);
    }

    @Override
    public <P,R> R accept(FxTreeVisitor2<P,R> visitor, P param) {
        return visitor.visitObj(this, param);
    }

    @Override
    public abstract int size();

    @Override
    public abstract Collection<FxNode> children();

    public abstract <T extends FxNode> T put(String name, Class<T> clss);

    public abstract <T extends FxNode> T get(String name);
    
    @Override
    public abstract void remove(FxNode child);

    public abstract FxNode remove(String name);

    public abstract void removeAll();

    public void removeAll(Collection<String> childNames) {
        for(String childName : childNames) {
            remove(childName);
        }
    }

    // helper methods for put(String name, Class<T> clss)
    // ------------------------------------------------------------------------

    public FxArrayNode putArray(String name) {
        return put(name, FxArrayNode.class);
    }

    public FxObjNode putObj(String name) {
        return put(name, FxObjNode.class);
    }

    public FxTextNode put(String name, String value) {
        FxTextNode res = put(name, FxTextNode.class);
        res.setValue(value);
        return res;
    }

    public FxDoubleNode put(String name, double value) {
        FxDoubleNode res = put(name, FxDoubleNode.class);
        res.setValue(value);
        return res;
    }
    
    public FxIntNode put(String name, int value) {
        FxIntNode res = put(name, FxIntNode.class);
        res.setValue(value);
        return res;
    }
    public FxBoolNode put(String name, boolean value) {
        FxBoolNode res = put(name, FxBoolNode.class);
        res.setValue(value);
        return res; 
    }
    
    public FxPOJONode putPOJO(String name, Object value) {
        FxPOJONode res = put(name, FxPOJONode.class);
        res.setValue(value);
        return res;
    }

    public FxNullNode putNull(String name) {
        return put(name, FxNullNode.class);
    }
    
}
