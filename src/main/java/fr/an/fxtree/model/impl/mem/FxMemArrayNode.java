package fr.an.fxtree.model.impl.mem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fr.an.fxtree.model.FXContainerNode;
import fr.an.fxtree.model.FxArrayNode;
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
    public void add(int index, FxNode node) {
        if (node.getParent() != null) {
            // detach from parent
            node.getParent().remove(node);
        }
        _children.add(index, node);
        int newChildId = childIdGenerator++;
        FxMemArrayInsertChildId childId = new FxMemArrayInsertChildId(newChildId, index);
        node._setParent(this, childId);
        reindexRemainingChildIds(index + 1);        
    }

    @Override
    public void add(FxNode child) {
        int len = _children.size();
        add(len, child);
    }

    @Override
    public void remove(FxNode child) {
        if (child.getParent() != this) throw new IllegalArgumentException();
        FxMemArrayInsertChildId childId = (FxMemArrayInsertChildId) child.getChildId();
        int index = childId.getCurrIndex();
        remove(index);
    }
    
    @Override
    public FxNode remove(int index) {
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
