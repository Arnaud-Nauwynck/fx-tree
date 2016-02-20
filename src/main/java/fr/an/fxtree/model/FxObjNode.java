package fr.an.fxtree.model;

import java.util.Collection;

public abstract class FxObjNode extends FXContainerNode {

    // ------------------------------------------------------------------------
    
    protected FxObjNode(FXContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitObj(this);
    }

    @Override
    public <P,R> R accept(FxTreeVisitor2<P,R> visitor, P param) {
        return visitor.visitObj(this, param);
    }

    @Override
    public abstract int size();

    @Override
    public abstract Collection<FxNode> children();

    public abstract void add(String name, FxNode node);

    @Override
    public abstract void remove(FxNode child);

    public abstract FxNode remove(String name);
    
}
