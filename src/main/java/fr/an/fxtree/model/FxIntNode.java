package fr.an.fxtree.model;

public abstract class FxIntNode extends FxValueNode {

    // ------------------------------------------------------------------------
    
    protected FxIntNode(FXContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.NUMBER;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitIntValue(this);
    }
    
    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitIntValue(this, param);
    }
    
    public abstract int getValue();

    public abstract void setValue(int value);

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return Integer.toString(getValue());
    }
    
}
