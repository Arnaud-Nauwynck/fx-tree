package fr.an.fxtree.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class FxRootDocument extends FXContainerNode {

    private FxNodeFactoryRegistry nodeFactory;
    
    private FxNode childContent;
    
    private Map<String,Object> extraParams = new HashMap<String,Object>();
    
    // ------------------------------------------------------------------------
    
    protected FxRootDocument(FxNodeFactoryRegistry nodeFactory) {
        super(null, null);
        if (nodeFactory == null) throw new IllegalArgumentException();
        this.nodeFactory = nodeFactory;
    }

    // ------------------------------------------------------------------------

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitRoot(this);
    }

    @Override
    public <P,R> R accept(FxTreeVisitor2<P,R> visitor, P param) {
        return visitor.visitRoot(this, param);
    }
    
    public FxNodeFactoryRegistry getNodeFactory() {
        return nodeFactory;
    }

    public FxNode getContent() {
        return childContent;
    }

    @Override
    public int size() {
        return childContent != null? 1 : 0;
    }

    @Override
    public Collection<FxNode> children() {
        return childContent != null? Collections.singleton(childContent) : Collections.emptyList();
    }

    @Override
    public void remove(FxNode node) {
        if (node != childContent) throw new IllegalArgumentException();
        childContent._setParent(null, null);
        this.childContent = null;
    }

    @Override
    public FxNode remove(FxChildId childId) {
        if (childContent == null || childId != childContent.getChildId()) throw new IllegalArgumentException();
        childContent._setParent(null, null);
        FxNode res = childContent;
        this.childContent = null;
        return res;
    }
    
    public void setContent(FxNode node) {
        if (node == childContent) return;
        if (childContent != null) {
            remove(childContent);
        }
        this.childContent = node;
        if (childContent != null) {
            node._setParent(this, null);
        }
    }

    public Object getExtraParam(String key) {
        return extraParams.get(key);
    }

    public Object putExtraParam(String key, Object value) {
        return extraParams.put(key, value);
    }

}

