package fr.an.fxtree.model;

import java.util.Collection;

public abstract class FXContainerNode extends FxNode {

    /** redundant with <code>parent.parent. ...</code>  (before null)*/
    protected FxRootDocument rootDocument;
    
    // ------------------------------------------------------------------------
    
    protected FXContainerNode(FXContainerNode parent, FxChildId childId) {
        super(parent, childId);
        this.rootDocument = (parent != null)? parent.rootDocument : null;
    }

    // ------------------------------------------------------------------------

    public abstract int size();

    public abstract Collection<FxNode> children();
    
    public abstract void remove(FxNode node);
    // public void add(FxChildId childId, FxNode node);

    public FxRootDocument getRootDocument() {
        return rootDocument;
    }

    public FxNodeFactory getNodeFactory() {
        return rootDocument.getNodeFactory();
    }
    
    @Override
    /*protected*/ public void _setParent(FXContainerNode parent, FxChildId childId) {
        if (parent != null && rootDocument != parent.rootDocument) {
            throw new IllegalArgumentException();
        }
        super._setParent(parent, childId);
    }

    public FxArrayNode newArray() {
        return getNodeFactory().newArray();
    }

    public FxObjNode newObj() {
        return getNodeFactory().newObj();
    }

    public FxTextNode newText() {
        return getNodeFactory().newText();
    }

    public FxDoubleNode newDouble() {
        return getNodeFactory().newDouble();
    }

    public FxIntNode newInt() {
        return getNodeFactory().newInt();
    }

    public FxBoolNode newBool() {
        return getNodeFactory().newBool();
    }

    public FxPOJONode newPOJO() {
        return getNodeFactory().newPOJO();
    }

    public FxNullNode newNull() {
        return getNodeFactory().newNull();
    }

}
