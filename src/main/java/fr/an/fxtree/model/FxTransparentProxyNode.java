package fr.an.fxtree.model;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;

/**
 * 
 */
public abstract class FxTransparentProxyNode extends FxNode {

    protected FxTransparentProxyNode(FxContainerNode parent, FxChildId childId, FxSourceLoc sourceLoc) {
        super(parent, childId, sourceLoc);
    }

    public abstract FxNode getTargetNode();
    
}
