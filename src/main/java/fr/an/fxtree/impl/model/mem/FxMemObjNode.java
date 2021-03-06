package fr.an.fxtree.impl.model.mem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.an.fxtree.impl.model.mem.FxMemChildId.FxMemObjNameChildId;
import fr.an.fxtree.model.FxChildId;
import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public class FxMemObjNode extends FxObjNode {

    private Map<String,FxNode> _children = new LinkedHashMap<>();
    
    // ------------------------------------------------------------------------
    
    protected FxMemObjNode(FxContainerNode parent, FxMemChildId childId, FxSourceLoc sourceLoc) {
        super(parent, childId, sourceLoc);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public int size() {
        return _children.size();
    }

    @Override
    public boolean isEmpty() {
        return _children.isEmpty();
    }

    @Override
    public Iterator<FxNode> childIterator() {
        return _children.values().iterator();
    }

    @Override
    public Iterator<Map.Entry<String, FxNode>> fields() {
        return _children.entrySet().iterator();
    }

    @Override
	public Map<String, FxNode> fieldsMap() {
        return new LinkedHashMap<>(_children);
    }

    @Override
	@SuppressWarnings("unchecked")
    public <T extends FxNode> T get(String name) {
        return (T) _children.get(name);
    }

//    @Override
//    public <T extends FxNode> T put(String name, Class<T> clss, FxSourceLoc loc) {
//        T res = getNodeFactory().newNode(clss, loc);
//        onPut(name, res);
//        return res;
//    }
//
    @Override
    protected <T extends FxNode> T onPut(String name, T node) {
        _children.put(name, node);
        node._setParent(this, FxMemObjNameChildId.of(name));
        return node;
    }

    @Override
    public FxNode remove(FxNode child) {
        if (child.getParent() != this) throw new IllegalArgumentException();
        FxMemObjNameChildId objChildId = (FxMemObjNameChildId) child.getChildId();
        return doRemove(objChildId.getName());
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
        List<FxNode> removeChildren = new ArrayList<>(_children.values());
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
