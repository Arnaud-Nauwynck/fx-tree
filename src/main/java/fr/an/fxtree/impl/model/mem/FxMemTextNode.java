package fr.an.fxtree.impl.model.mem;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxTextNode;

public class FxMemTextNode extends FxTextNode {

    private String value;
    
    // ------------------------------------------------------------------------
    
    protected FxMemTextNode(FxContainerNode parent, FxMemChildId childId, FxSourceLoc sourceLoc, String value) {
        super(parent, childId, sourceLoc);
        this.value = value;
    }

    // ------------------------------------------------------------------------
        
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
