package fr.an.fxtree.model;

public abstract class FxTextNode extends FxValueNode {

    // ------------------------------------------------------------------------
    
    protected FxTextNode(FXContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitTextValue(this);
    }
    
    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitTextValue(this, param);
    }
    
    public abstract String getValue();

    public abstract void setValue(String value);

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getValue();
    }
    
}
