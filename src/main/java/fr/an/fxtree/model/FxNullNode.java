package fr.an.fxtree.model;

public class FxNullNode extends FxValueNode {
    
    // ------------------------------------------------------------------------
    
    protected FxNullNode(FXContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitNullValue(this);
    }
    
    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitNullValue(this, param);
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "null";
    }
}
