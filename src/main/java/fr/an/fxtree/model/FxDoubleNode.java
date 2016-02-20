package fr.an.fxtree.model;

public abstract class FxDoubleNode extends FxValueNode {
    
    // ------------------------------------------------------------------------
    
    protected FxDoubleNode(FXContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitDoubleValue(this);
    }
    
    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitDoubleValue(this, param);
    }
    
    public abstract double getValue();

    public abstract void setValue(double value);

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return Double.toString(getValue());
    }
}
