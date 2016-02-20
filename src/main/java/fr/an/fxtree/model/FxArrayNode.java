package fr.an.fxtree.model;

import java.util.Collection;

public abstract class FxArrayNode extends FXContainerNode {

    // ------------------------------------------------------------------------
    
    protected FxArrayNode(FXContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitArray(this);
    }

    @Override
    public <P,R> R accept(FxTreeVisitor2<P,R> visitor, P param) {
        return visitor.visitArray(this, param);
    }

    @Override
    public abstract int size();
    
    public abstract FxNode get(int index);

    public abstract Collection<FxNode> children();

    public abstract void add(int index, FxNode node);

    public abstract void add(FxNode child);

    @Override
    public abstract void remove(FxNode child);
    
    public abstract FxNode remove(int index);
    
}
