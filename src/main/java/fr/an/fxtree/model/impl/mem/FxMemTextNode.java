package fr.an.fxtree.model.impl.mem;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxTextNode;
import fr.an.fxtree.model.FxTreeVisitor;
import fr.an.fxtree.model.FxTreeVisitor2;

public class FxMemTextNode extends FxTextNode {

    private String value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemTextNode(FXContainerNode parent, FxMemChildId childId, String value) {
        super(parent, childId);
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitTextValue(this);
    }
    
    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitTextValue(this, param);
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return value;
    }
    
}
