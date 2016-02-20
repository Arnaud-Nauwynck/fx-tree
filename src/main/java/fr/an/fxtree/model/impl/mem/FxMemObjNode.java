package fr.an.fxtree.model.impl.mem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxChildId;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.impl.mem.FxMemChildId.FxMemObjNameChildId;

public class FxMemObjNode extends FxObjNode {

    private Map<String,FxNode> _children = new LinkedHashMap<String,FxNode>();
    
    // ------------------------------------------------------------------------
    
    protected FxMemObjNode(FXContainerNode parent, FxMemChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public int size() {
        return _children.size();
    }

    @Override
    public Collection<FxNode> children() {
        return _children.values();
    }

    @SuppressWarnings("unchecked")
    public <T extends FxNode> T get(String name) {
        return (T) _children.get(name);
    }
    
    @Override
    public <T extends FxNode> T put(String name, Class<T> clss) {
        T res = getNodeFactory().newNode(clss);
        _children.put(name, res);
        res._setParent(this, new FxMemObjNameChildId(name));
        return res;
    }

    @Override
    public void remove(FxNode child) {
        if (child.getParent() != this) throw new IllegalArgumentException();
        FxMemObjNameChildId objChildId = (FxMemObjNameChildId) child.getChildId();
        doRemove(objChildId.getName());
    }

    @Override
    public FxNode remove(FxChildId childId) {
        if (!(childId instanceof FxMemObjNameChildId)) throw new IllegalArgumentException();
        FxMemObjNameChildId objChildId = (FxMemObjNameChildId) childId;
        return doRemove(objChildId.getName());
    }
    
    @Override
    public FxNode remove(String name) {
        return doRemove(name);
    }
    
    @Override
    public void removeAll() {
        List<FxNode> removeChildren = new ArrayList<FxNode>(_children.values());
        _children.clear();
        for (FxNode child : removeChildren) {
            child._setParent(null, null);
        }
    }
    
    protected FxNode doRemove(String name) {
        FxNode res = _children.remove(name);
        if (res != null) {
            res._setParent(null, null);
        }
        return res;
    }
    
}
