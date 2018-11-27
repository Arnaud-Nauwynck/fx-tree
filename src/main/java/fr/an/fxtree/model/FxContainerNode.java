package fr.an.fxtree.model;

import java.util.Iterator;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;

public abstract class FxContainerNode extends FxNode {

    protected FxNodeFactoryRegistry nodeFactory;

    // ------------------------------------------------------------------------

    protected FxContainerNode(FxContainerNode parent, FxChildId childId, FxSourceLoc sourceLoc) {
        super(parent, childId, sourceLoc);
        this.nodeFactory = (parent != null) ? parent.getNodeFactory() : null;
    }

    @Override
    /* protected */ public void _setParent(FxContainerNode parent, FxChildId childId) {
        if (parent != null) {
            this.nodeFactory = parent.nodeFactory;
        } else {
            this.nodeFactory = null;
        }
        super._setParent(parent, childId);
    }

    // ------------------------------------------------------------------------

    public abstract int size();

    public abstract boolean isEmpty();

    public abstract Iterator<FxNode> childIterator();

    public FxNodeFactoryRegistry getNodeFactory() {
        return nodeFactory;
    }

    @Override
    public String asText() {
        return "";
    }

}
