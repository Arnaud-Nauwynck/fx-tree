package fr.an.fxtree.impl.model.proxy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.an.fxtree.impl.util.LigthweightMapEntry;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxBoolNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxChildId;
import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxDoubleNode;
import fr.an.fxtree.model.FxIntNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxNodeType;
import fr.an.fxtree.model.FxNullNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.FxTextNode;

public class FxReadObjNodeProxy extends FxObjNode /*implements IFxObjNodeReader*/ {

    protected FxObjNode delegate;
    protected boolean allowGetParent;

    // ------------------------------------------------------------------------
    
    public FxReadObjNodeProxy(FxContainerNode proxyParent, boolean allowGetParent, FxObjNode delegate) {
        super(proxyParent, null);
        this.allowGetParent = allowGetParent;
        this.delegate = delegate;
    }

    // mixin "inherit from _FxContainerNodeProxyMixin" ?? copy-paste code...
    // ------------------------------------------------------------------------

    protected <T extends FxNode> T wrapChild(T node) {
        if (node == null) return null;
        return FxReadProxyNodeWrappers.wrapROProxy(this, allowGetParent, node);
    }
    
    protected RuntimeException throwWriteDenied() {
        throw FxReadProxyNodeWrappers.throwWriteDenied();
    }


    @Override
    public FxContainerNode getParent() {
        if (!allowGetParent) FxReadProxyNodeWrappers.throwGetParentDenied(); 
        return super.getParent();
    }

    @Override
    public void _setParent(FxContainerNode parent, FxChildId childId) {
        super._setParent(parent, childId);
        //?? this.rootDocument = null;
    }

    @Override
    public FxNodeType getNodeType() {
        return delegate.getNodeType();
    }
    
    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }
        
    // @Override
    public Collection<FxNode> children() {
        List<FxNode> res = new ArrayList<>(delegate.size());
        for (Iterator<FxNode> delegateIter = delegate.childIterator(); delegateIter.hasNext(); ) {
            FxNode delegateChild = delegateIter.next();
            res.add(wrapChild(delegateChild));
        }
        return Collections.unmodifiableList(res);
    }

    @Override
    public Iterator<FxNode> childIterator() {
        Iterator<FxNode> delegateIterator = delegate.childIterator();
        return new Iterator<FxNode>() {
            @Override
            public boolean hasNext() {
                return delegateIterator.hasNext();
            }

            @Override
            public FxNode next() {
                return wrapChild(delegateIterator.next());
            }

            @Override
            public void remove() {
                throw throwWriteDenied();
            }
            
        };
    }

    // specific for FxObjNode implements/override
    // ------------------------------------------------------------------------
    
    @Override
    public Iterator<Map.Entry<String, FxNode>> fields() {
        Iterator<Map.Entry<String, FxNode>> delegateIterator = delegate.fields();
        return new Iterator<Map.Entry<String, FxNode>>() {
            LigthweightMapEntry<String,FxNode> reusableMapEntry = new LigthweightMapEntry<String,FxNode>(null, null);

            @Override
            public boolean hasNext() {
                return delegateIterator.hasNext();
            }

            @Override
            public Map.Entry<String, FxNode> next() {
                Entry<String, FxNode> e = delegateIterator.next();
                String fieldName = e.getKey();
                FxNode wrapNode = wrapChild(e.getValue());
                // return new ImmutableMapEntry<String, FxNode>(fieldName, wrapNode);
                reusableMapEntry._setCurr(fieldName, wrapNode);
                return reusableMapEntry;
            }

            @Override
            public void remove() {
                throw throwWriteDenied();
            }
            
        };
    }

    @Override
    public Map<String, FxNode> fieldsHashMapCopy() {
        Map<String, FxNode> res = new HashMap<>(delegate.size());
        for(Iterator<Map.Entry<String,FxNode>> iter = delegate.fields(); iter.hasNext(); ) {
            Entry<String, FxNode> e = iter.next();
            res.put(e.getKey(), wrapChild(e.getValue()));
        }
        return Collections.unmodifiableMap(res);
    }

    @Override
    public void forEachFields(FieldFunc callback) {
        for(Iterator<Map.Entry<String,FxNode>> iter = delegate.fields(); iter.hasNext(); ) {
            Entry<String, FxNode> e = iter.next();
            callback.onField(e.getKey(), wrapChild(e.getValue()));
        }
    }

    @Override
    public <T extends FxNode> T get(String name) {
        T tmpres = delegate.get(name);
        return wrapChild(tmpres);
    }

    @Override
    public <T extends FxNode> T put(String name, Class<T> clss) {
        throw throwWriteDenied();
    }

    @Override
    protected <T extends FxNode> T onPut(String name, T node) {
        throw throwWriteDenied();
    }

    @Override
    public void remove(FxNode child) {
        throw throwWriteDenied();
    }

    @Override
    public FxNode remove(String name) {
        throw throwWriteDenied();
    }

    @Override
    public void removeAll() {
        throw throwWriteDenied();
    }

    @Override
    public void removeAll(Collection<String> childNames) {
        throw throwWriteDenied();
    }

    @Override
    public FxChildWriter putBuilder(String name) {
        throw throwWriteDenied();
    }

    @Override
    public FxArrayNode putArray(String name) {
        throw throwWriteDenied();
    }

    @Override
    public FxObjNode putObj(String name) {
        throw throwWriteDenied();
    }

    @Override
    public FxTextNode put(String name, String value) {
        throw throwWriteDenied();
    }

    @Override
    public FxDoubleNode put(String name, double value) {
        throw throwWriteDenied();
    }

    @Override
    public FxIntNode put(String name, int value) {
        throw throwWriteDenied();
    }

    @Override
    public FxBoolNode put(String name, boolean value) {
        throw throwWriteDenied();
    }

    @Override
    public FxBinaryNode put(String name, byte[] value) {
        throw throwWriteDenied();
    }

    @Override
    public FxPOJONode put(String name, BigInteger value) {
        throw throwWriteDenied();
    }

    @Override
    public FxPOJONode put(String name, BigDecimal value) {
        throw throwWriteDenied();
    }

    @Override
    public FxPOJONode putPOJO(String name, Object value) {
        throw throwWriteDenied();
    }

    @Override
    public FxNullNode putNull(String name) {
        throw throwWriteDenied();
    }

    @Override
    public FxNode remove(FxChildId childId) {
        throw throwWriteDenied();
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "FxReadObjNodeProxy [delegate=" + delegate + "]";
    }
    
}
