package fr.an.fxtree.model;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.path.FxNodeOuterPath;

public abstract class FxLinkProxyNode extends FxTransparentProxyNode {

    // ------------------------------------------------------------------------

    public FxLinkProxyNode(FxContainerNode parent, FxChildId childId, FxSourceLoc sourceLoc) {
        super(parent, childId, sourceLoc);
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitLink(this);
    }

    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitLink(this, param);
    }

    @Override
    public FxNodeType getNodeType() {
        return FxNodeType.LINK;
    }
    
    public abstract FxNodeOuterPath getTargetRelativePath();

    public abstract void setTargetRelativePath(FxNodeOuterPath p);
    
}
