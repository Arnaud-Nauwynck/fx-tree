package fr.an.fxtree.model.impl.mem;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxTreeVisitor;
import fr.an.fxtree.model.FxTreeVisitor2;
import fr.an.fxtree.model.impl.mem.FxMemChildId.FxMemObjNameChildId;

public class FxMemObjNode extends FxObjNode {

    private Map<String,FxNode> _children = new LinkedHashMap<String,FxNode>();
    
    // ------------------------------------------------------------------------
    
    protected FxMemObjNode(FXContainerNode parent, FxMemChildId childId) {
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
    public int size() {
        return _children.size();
    }

    @Override
    public Collection<FxNode> children() {
        return _children.values();
    }

    @Override
    public void add(String name, FxNode node) {
        if (node.getParent() != null) {
            node.getParent().remove(node);
        }
        _children.put(name, node);
        node._setParent(this, new FxMemObjNameChildId(name));
    }

    @Override
    public void remove(FxNode child) {
        if (child.getParent() != this) throw new IllegalArgumentException();
        FxMemObjNameChildId childId = (FxMemObjNameChildId) child.getChildId();
        String name = childId.getName();
        remove(name);
    }

    @Override
    public FxNode remove(String name) {
        FxNode res = _children.remove(name);
        if (res != null) {
            res._setParent(null, null);
        }
        return res;
    }
    
}
