package fr.an.fxtree.impl.model.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fr.an.fxtree.model.FxChildId;
import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxNode;

/**
 * Dummy class MIXIN !!!!
 * real mixin does not exist in java ..7,8,9,10...
 * so code is Copy&Paste in 2 sub-classes: FxReadArrayNodeProxy and FxReadObjNodeProxy
 */
public abstract class _FxReadContainerNodeProxyMixin extends FxContainerNode {

    protected FxContainerNode delegate;
    protected boolean allowGetParent;

    public _FxReadContainerNodeProxyMixin(FxContainerNode proxyParent, boolean allowGetParent, FxContainerNode delegate) {
        super(proxyParent, null);
        this.delegate = delegate;
        this.allowGetParent = allowGetParent;
    }

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

    // @Override
    public Collection<FxNode> children() {
        List<FxNode> res = new ArrayList<>(delegate.size());
        for (Iterator<FxNode> iter = delegate.childIterator(); iter.hasNext(); ) {
            FxNode delegateChild = iter.next();
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

    @Override
    public void remove(FxNode chld) {
        throw throwWriteDenied();
    }

    @Override
    public FxNode remove(FxChildId childId) {
        throw throwWriteDenied();
    }

}
