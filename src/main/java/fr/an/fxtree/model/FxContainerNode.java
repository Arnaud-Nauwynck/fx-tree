package fr.an.fxtree.model;

import java.util.Collection;
import java.util.Iterator;

public abstract class FxContainerNode extends FxNode {

    /** redundant with <code>parent.parent. ...</code> (before null) */
    protected FxRootDocument rootDocument;

    // ------------------------------------------------------------------------

    protected FxContainerNode(FxContainerNode parent, FxChildId childId) {
        super(parent, childId);
        this.rootDocument = (parent != null) ? parent.rootDocument : null;
    }

    @Override
    /* protected */ public void _setParent(FxContainerNode parent, FxChildId childId) {
        if (parent != null) {
            this.rootDocument = parent.rootDocument;
        } else {
            this.rootDocument = null;
        }
        super._setParent(parent, childId);
    }

    // ------------------------------------------------------------------------

    public abstract int size();

    public abstract boolean isEmpty();

    public abstract Collection<FxNode> children();

    public abstract Iterator<FxNode> childIterator();

    public abstract void remove(FxNode chld);

    public abstract FxNode remove(FxChildId childId);

    protected FxRootDocument getRootDocument() {
        return rootDocument;
    }

    public FxNodeFactoryRegistry getNodeFactory() {
        return rootDocument.getNodeFactory();
    }

    @Override
    public String asText() {
        return "";
    }

}
