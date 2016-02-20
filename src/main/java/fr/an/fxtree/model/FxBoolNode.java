package fr.an.fxtree.model;

public abstract class FxBoolNode extends FxValueNode {

    // ------------------------------------------------------------------------
    
    protected FxBoolNode(FXContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.BOOLEAN;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitBoolValue(this);
    }
    
    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitBoolValue(this, param);
    }
    
    public abstract boolean getValue();

    public abstract void setValue(boolean value);

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return Boolean.toString(getValue());
    }
    
}
