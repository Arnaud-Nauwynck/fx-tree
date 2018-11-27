package fr.an.fxtree.impl.model.mem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.Flat3Map;

import fr.an.fxtree.impl.model.mem.FxMemChildId.FxMemObjNameChildId;
import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;


public class FxMemObjNode2 extends FxObjNode {

    private Flat3Map/*<String,FxNode>*/ _children = new Flat3Map/*<>*/();
    
    // ------------------------------------------------------------------------
    
    protected FxMemObjNode2(FxContainerNode parent, FxMemChildId childId, FxSourceLoc sourceLoc) {
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

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<FxNode> childIterator() {
        return _children.values().iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Map.Entry<String, FxNode>> fields() {
        return _children.entrySet().iterator();
    }

//    public Map<String, FxNode> fieldsHashMapCopy() {
//        return new LinkedHashMap<>(_children);
//    }
    
    @SuppressWarnings("unchecked")
    public <T extends FxNode> T get(String name) {
        return (T) _children.get(name);
    }
    
    @Override
    protected <T extends FxNode> T onPut(String name, T node) {
        _children.put(name, node);
        node._setParent(this, FxMemObjNameChildId.of(name));
        return node;
    }
    
    @Override
    public FxNode remove(String name) {
        return doRemove(name);
    }
    
    @Override
    public void removeAll() {
        List<FxNode> removeChildren = new ArrayList<FxNode>(_children.size());
        for(Iterator<FxNode> iter = childIterator(); iter.hasNext(); ) {
            removeChildren.add(iter.next());
        }
        _children.clear();
        for (FxNode child : removeChildren) {
            child._setParent(null, null);
        }
    }
    
    protected FxNode doRemove(String name) {
        FxNode res = (FxNode) _children.remove(name);
        if (res != null) {
            res._setParent(null, null);
        }
        return res;
    }
    
}
