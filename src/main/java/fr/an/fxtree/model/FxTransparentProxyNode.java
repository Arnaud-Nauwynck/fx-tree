package fr.an.fxtree.model;

/**
 *
 */
public abstract class FxTransparentProxyNode extends FxNode {

    protected FxTransparentProxyNode(FxContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    public abstract FxNode getTargetNode();

}
