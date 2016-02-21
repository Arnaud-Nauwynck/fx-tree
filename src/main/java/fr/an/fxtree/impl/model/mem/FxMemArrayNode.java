package fr.an.fxtree.impl.model.mem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.impl.model.mem.FxMemChildId.FxMemArrayInsertChildId;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildId;
import fr.an.fxtree.model.FxNode;

public class FxMemArrayNode extends FxArrayNode {

    private List<FxNode> _children = new ArrayList<>();

    private int childIdGenerator = 1;
    
    // ------------------------------------------------------------------------
    
    protected FxMemArrayNode(FxContainerNode parent, FxMemChildId childId) {
        super(parent, childId);
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
    public FxNode get(int index) {
        return _children.get(index);
    }

    @Override
    public Collection<FxNode> children() {
        return Collections.unmodifiableList(_children);
    }

    @Override
    public Iterator<FxNode> childIterator() {
        return _children.iterator();
    }

    @Override
    public <T extends FxNode> T insert(int index, Class<T> clss) {
        T res = getNodeFactory().newNode(clss);
        return onInsert(index, res);
    }

    protected <T extends FxNode> T onInsert(int index, T node) {
        int newChildId = childIdGenerator++;
        FxMemArrayInsertChildId childId = new FxMemArrayInsertChildId(newChildId, index);
        _children.add(index, node);
        node._setParent(this, childId);
        reindexRemainingChildIds(index + 1);
        return node;
    }

    
    @Override
    public void remove(FxNode child) {
        if (child.getParent() != this) throw new IllegalArgumentException();
        FxMemArrayInsertChildId childId = (FxMemArrayInsertChildId) child.getChildId();
        doRemove(childId.getCurrIndex());
    }

    @Override
    public FxNode remove(FxChildId childId) {
        if (!(childId instanceof FxMemArrayInsertChildId)) throw new IllegalArgumentException();
        FxMemArrayInsertChildId arrayChildId = (FxMemArrayInsertChildId) childId;
        return doRemove(arrayChildId.getCurrIndex());
    }

    @Override
    public FxNode remove(int index) {
        return doRemove(index);
    }

    public void removeAll() {
        int len = size();
        for(int i = len-1; i >= 0; i--) {
            FxNode res = _children.get(i);
            res._setParent(null,  null);
        }
        _children.clear();
    }

    protected FxNode doRemove(int index) {
        FxNode res = _children.remove(index);
        res._setParent(null,  null);
        reindexRemainingChildIds(index);
        return res;
    }
    
    private void reindexRemainingChildIds(int index) {
        final int len = _children.size();
        for(int i = index; i < len; i++) {
            // reindex remainings
            FxMemArrayInsertChildId childId = (FxMemArrayInsertChildId) _children.get(i).getChildId();
            childId._setCurrIndex(i);
        }
    }
    
    
}
