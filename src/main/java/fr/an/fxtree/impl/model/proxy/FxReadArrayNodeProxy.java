package fr.an.fxtree.impl.model.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.reader.IFxArrayNodeReader;

public class FxReadArrayNodeProxy extends FxArrayNode implements IFxArrayNodeReader {

    protected FxArrayNode delegate;
    protected boolean allowGetParent;
    
    // ------------------------------------------------------------------------
    
    protected FxReadArrayNodeProxy(FxContainerNode proxyParent, FxSourceLoc sourceLoc, boolean allowGetParent, FxArrayNode delegate) {
        super(proxyParent, null, sourceLoc);
        this.allowGetParent = allowGetParent;
        this.delegate = delegate;
    }

    // mixin "inherit from _FxContainerNodeProxyMixin" ?? copy-paste code...
    // ------------------------------------------------------------------------

    protected FxNode wrapChild(FxNode node) {
        if (node == null) return null;
        return FxReadProxyNodeWrappers.wrapROProxy(this, allowGetParent, node);
    }
    
    protected RuntimeException throwWriteDenied() {
        throw FxReadProxyNodeWrappers.throwWriteDenied();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }
        
    @Override
    public Collection<FxNode> children() {
        List<FxNode> res = new ArrayList<>(delegate.size());
        for (Iterator<FxNode> delegateIterator = delegate.childIterator(); delegateIterator.hasNext(); ) {
            FxNode delegateChild = delegateIterator.next();
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

    // specific for FxArrayNode implements/override
    // ------------------------------------------------------------------------
    
    @Override
    public FxNode get(int index) {
        return wrapChild(delegate.get(index));
    }

    // implements abstract FxArrayNode
    // ------------------------------------------------------------------------

    @Override
    protected <T extends FxNode> T onInsert(int index, T node) {
        throw throwWriteDenied();
    }
    
    @Override
    public FxNode remove(int index) {
        throw throwWriteDenied();
    }

    @Override
    public void removeAll() {
        throw throwWriteDenied();
    }

}
