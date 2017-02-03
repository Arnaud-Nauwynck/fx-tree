package fr.an.fxtree.impl.model.mem;

import java.util.List;

import fr.an.fxtree.model.FxChildId;
import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxLinkProxyNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.path.FxNodeOuterPath;

public class FxMemLinkProxyNode extends FxLinkProxyNode {

    protected FxNodeOuterPath targetRelativePath;

    protected FxNode targetNode;

    // ------------------------------------------------------------------------

    public FxMemLinkProxyNode(FxContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    public FxMemLinkProxyNode(FxContainerNode parent, FxChildId childId, FxNodeOuterPath targetRelativePath) {
        this(parent, childId);
        this.targetRelativePath = targetRelativePath;
    }

    // ------------------------------------------------------------------------

    @Override
	public FxNodeOuterPath getTargetRelativePath() {
        return targetRelativePath;
    }

    @Override
	public void setTargetRelativePath(FxNodeOuterPath p) {
        if (this.targetRelativePath == p) {
            return;
        }
        this.targetRelativePath = p;
        this.targetNode = null;
    }

    @Override
    public FxNode getTargetNode() {
        return targetNode;
    }

    public void setResolveTargetNodeFromStack(List<FxNode> stack) {
        if (targetNode == null) {
            targetNode = targetRelativePath.selectFromStack(stack);
        }
    }

    @Override
    public String asText() {
        return "Link:" + targetRelativePath;
    }

}
