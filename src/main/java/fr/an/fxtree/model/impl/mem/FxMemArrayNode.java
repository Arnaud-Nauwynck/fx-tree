package fr.an.fxtree.model.impl.mem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildId;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.impl.mem.FxMemChildId.FxMemArrayInsertChildId;

public class FxMemArrayNode extends FxArrayNode {

    private List<FxNode> _children = new ArrayList<>();

    private int childIdGenerator = 1;
    
    // ------------------------------------------------------------------------
    
    protected FxMemArrayNode(FXContainerNode parent, FxMemChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public int size() {
        return _children.size();
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
    public <T extends FxNode> T insert(int index, Class<T> clss) {
        int newChildId = childIdGenerator++;
        FxMemArrayInsertChildId childId = new FxMemArrayInsertChildId(newChildId, index);
        T res = getNodeFactory().newNode(clss);
        _children.add(index, res);
        res._setParent(this, childId);
        reindexRemainingChildIds(index + 1);
        return res;
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
