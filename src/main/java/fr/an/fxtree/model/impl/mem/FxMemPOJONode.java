package fr.an.fxtree.model.impl.mem;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.FxTreeVisitor;
import fr.an.fxtree.model.FxTreeVisitor2;

public class FxMemPOJONode extends FxPOJONode {

    private Object value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemPOJONode(FXContainerNode parent, FxMemChildId childId, Object value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitPOJOValue(this);
    }
    
    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitPOJOValue(this, param);
    }
    
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return (value != null)? value.toString() : "null";
    }
    
}
