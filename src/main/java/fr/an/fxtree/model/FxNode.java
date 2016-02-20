package fr.an.fxtree.model;

public abstract class FxNode {

    private FXContainerNode parent;
    private FxChildId childId;
    
    // ------------------------------------------------------------------------
    
    protected FxNode(FXContainerNode parent, FxChildId childId) {
        this.parent = parent;
        this.childId = childId;
    }

    // ------------------------------------------------------------------------

    public abstract void accept(FxTreeVisitor visitor);

    public abstract <P,R> R accept(FxTreeVisitor2<P,R> visitor, P param);

    public FXContainerNode getParent() {
        return parent;
    }

    public FxChildId getChildId() {
        return childId;
    }

    /*protected*/ public void _setParent(FXContainerNode parent, FxChildId childId) {
        this.parent = parent;
        this.childId = childId;
    }
    
    
    public abstract FxNodeType getNodeType();

    public final boolean isValueNode() {
        switch (getNodeType()) {
            case ARRAY: case OBJECT: case MISSING:
                return false;
            default:
                return true;
        }
    }

    public final boolean isContainerNode() {
        final FxNodeType type = getNodeType();
        return type == FxNodeType.OBJECT || type == FxNodeType.ARRAY;
    }

    public final boolean isMissingNode() {
        return getNodeType() == FxNodeType.MISSING;
    }

    public final boolean isArray() {
        return getNodeType() == FxNodeType.ARRAY;
    }

    public final boolean isObject() {
        return getNodeType() == FxNodeType.OBJECT;
    }
    
}
