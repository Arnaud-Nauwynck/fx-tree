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
    
    public abstract void remove(FxNode chld);

    public abstract FxNode remove(FxChildId childId);

    protected FxRootDocument getRootDocument() {
        return rootDocument;
    }

    protected FxNodeFactoryRegistry getNodeFactory() {
        return rootDocument.getNodeFactory();
    }
    
    @Override
    /*protected*/ public void _setParent(FXContainerNode parent, FxChildId childId) {
        if (parent != null && rootDocument != parent.rootDocument) {
            throw new IllegalArgumentException();
        }
        super._setParent(parent, childId);
    }
    
}
