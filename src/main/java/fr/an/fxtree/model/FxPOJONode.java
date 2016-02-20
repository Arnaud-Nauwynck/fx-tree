package fr.an.fxtree.model;

public abstract class FxPOJONode extends FxValueNode {
    
    // ------------------------------------------------------------------------
    
    protected FxPOJONode(FXContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitPOJOValue(this);
    }
    
    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitPOJOValue(this, param);
    }
    
    public abstract Object getValue();

    public abstract void setValue(Object value);

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        Object value = getValue();
        return (value != null)? value.toString() : "null";
    }
    
}
