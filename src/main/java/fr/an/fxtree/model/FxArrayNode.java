package fr.an.fxtree.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.path.FxNodeOuterPath;

public abstract class FxArrayNode extends FxContainerNode {

    // ------------------------------------------------------------------------

    protected FxArrayNode(FxContainerNode parent, FxChildId childId, FxSourceLoc sourceLoc) {
        super(parent, childId, sourceLoc);
    }

    // ------------------------------------------------------------------------

    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.ARRAY;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitArray(this);
    }

    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitArray(this, param);
    }

    @Override
    public abstract int size();

    public abstract FxNode get(int index);

    public abstract Collection<FxNode> children();

    public abstract Iterator<FxNode> childIterator();

    protected abstract <T extends FxNode> T onInsert(int index, T node);

    public abstract FxNode remove(int index);

    public void removeAll() {
        int len = size();
        for(int i = len-1; i >= 0; i--) {
            remove(i);
        }
    }

    // helper methods for insert(int index, Class<T> clss) or add(Class<T> clss)
    // ------------------------------------------------------------------------

    public FxChildWriter insertBuilder(int index) {
        return new InnerArrayChildWriter(index);
    }

    public FxChildWriter insertBuilder() {
        return new InnerArrayChildWriter(0);
    }
    
    public FxArrayNode insertArray(int index, FxSourceLoc loc) {
        FxArrayNode res = getNodeFactory().newArray(loc);
        return onInsert(index, res);
    }

    public FxArrayNode addArray(FxSourceLoc loc) {
        return insertArray(size(), loc);
    }

    public FxObjNode insertObj(int index, FxSourceLoc loc) {
        FxObjNode res = getNodeFactory().newObj(loc);
        return onInsert(index, res);
    }
    
    public FxObjNode addObj(FxSourceLoc loc) {
        return insertObj(size(), loc);
    }

    public FxTextNode insert(int index, String value, FxSourceLoc loc) {
        FxTextNode res = getNodeFactory().newText(value, loc);
        return onInsert(index, res);
    }

    public FxTextNode add(String value, FxSourceLoc loc) {
        return insert(size(), value, loc);
    }

    public FxDoubleNode insert(int index, double value, FxSourceLoc loc) {
        FxDoubleNode res = getNodeFactory().newDouble(value, loc);
        return onInsert(index, res);
    }

    public FxDoubleNode add(double value, FxSourceLoc loc) {
        return insert(size(), value, loc);
    }

    public FxIntNode insert(int index, int value, FxSourceLoc loc) {
        FxIntNode res = getNodeFactory().newInt(value, loc);
        return onInsert(index, res);
    }

    public FxIntNode add(int value, FxSourceLoc loc) {
        return insert(size(), value, loc);
    }

    public FxLongNode insert(int index, long value, FxSourceLoc loc) {
        FxLongNode res = getNodeFactory().newLong(value, loc);
        return onInsert(index, res);
    }

    public FxLongNode add(long value, FxSourceLoc loc) {
        return insert(size(), value, loc);
    }

    public FxBoolNode insert(int index, boolean value, FxSourceLoc loc) {
        FxBoolNode res = getNodeFactory().newBool(value, loc);
        return onInsert(index, res);
    }

    public FxBoolNode add(boolean value, FxSourceLoc loc) {
        return insert(size(), value, loc);
    }

    public FxBinaryNode insert(int index, byte[] value, FxSourceLoc loc) {
        FxBinaryNode res = getNodeFactory().newBinary(value, loc);
        return onInsert(index, res);
    }

    public FxBinaryNode add(byte[] value, FxSourceLoc loc) {
        return insert(size(), value, loc);
    }

    public FxPOJONode insert(int index, BigInteger value, FxSourceLoc loc) {
        FxPOJONode res = getNodeFactory().newPOJO(value, loc); // use POJO?
        return onInsert(index, res);
    }

    public FxPOJONode add(BigInteger value, FxSourceLoc loc) {
        return insert(size(), value, loc);
    }

    public FxPOJONode insert(int index, BigDecimal value, FxSourceLoc loc) {
        FxPOJONode res = getNodeFactory().newPOJO(value, loc); // use POJO?
        return onInsert(index, res);
    }

    public FxPOJONode add(BigDecimal value, FxSourceLoc loc) {
        return insert(size(), value, loc);
    }

    public FxPOJONode insertPOJO(int index, Object value, FxSourceLoc loc) {
        FxPOJONode res = getNodeFactory().newPOJO(value, loc);
        return onInsert(index, res);
    }

    public FxPOJONode addPOJO(Object value, FxSourceLoc loc) {
        return insertPOJO(size(), value, loc);
    }

    public FxLinkProxyNode insertLink(int index, FxNodeOuterPath value, FxSourceLoc loc) {
        FxLinkProxyNode res = getNodeFactory().newLink(value, loc);
        return onInsert(index, res);
    }

    public FxLinkProxyNode addLink(FxNodeOuterPath value, FxSourceLoc loc) {
        return insertLink(size(), value, loc);
    }

    public FxNullNode insertNull(int index, FxSourceLoc loc) {
        FxNullNode res = getNodeFactory().newNull(loc);
        return onInsert(index, res);
    }

    public FxNullNode addNull(FxSourceLoc loc) {
        return insertNull(size(), loc);
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof FxArrayNode) {
            return children().equals(((FxArrayNode) o).children());
        }
        return false;
    }

    protected boolean _childrenEqual(FxArrayNode other) {
        return children().equals(other.children());
    }

    @Override
    public int hashCode() {
        return children().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(16 + (size() << 4));
        sb.append('[');
        Iterator<FxNode> iter = childIterator();
        if (iter.hasNext()) {
            FxNode e = iter.next();
            sb.append(e);
        }
        for (; iter.hasNext(); ) {
            FxNode e = iter.next();
            sb.append(',');
            sb.append(e);
        }
        sb.append(']');
        return sb.toString();
    }
    
    // internal
    // ------------------------------------------------------------------------

    private final class InnerArrayChildWriter extends FxChildWriter {
        private int currIndex;
        
        public InnerArrayChildWriter(int index) {
            this.currIndex = index;
        }

        @Override
        protected FxSourceLoc insertLoc() {
            return FxArrayNode.this.getSourceLoc();
        }

        protected int incrIndex() {
            return currIndex++;
        }
        
        @Override
        public void remove() {
            FxArrayNode.this.remove(currIndex);
        }
        
        @Override
        public FxNode getResultChild() {
            return FxArrayNode.this.get(currIndex-1);
        }
        
        @Override
        public FxArrayNode addArray(FxSourceLoc loc) {
            return insertArray(incrIndex(), loc);
        }

        @Override
        public FxObjNode addObj(FxSourceLoc loc) {
            return insertObj(incrIndex(), loc);
        }

        @Override
        public FxTextNode add(String value, FxSourceLoc loc) {
            return insert(incrIndex(), value, loc);
        }

        @Override
        public FxDoubleNode add(double value, FxSourceLoc loc) {
            return insert(incrIndex(), value, loc);
        }

        @Override
        public FxIntNode add(int value, FxSourceLoc loc) {
            return insert(incrIndex(), value, loc);
        }

        @Override
        public FxLongNode add(long value, FxSourceLoc loc) {
            return insert(incrIndex(), value, loc);
        }

        @Override
        public FxBoolNode add(boolean value, FxSourceLoc loc) {
            return insert(incrIndex(), value, loc);
        }

        @Override
        public FxBinaryNode add(byte[] value, FxSourceLoc loc) {
            return insert(incrIndex(), value, loc);
        }

        @Override
        public FxPOJONode add(BigInteger value, FxSourceLoc loc) {
            return insert(incrIndex(), value, loc);
        }

        @Override
        public FxPOJONode add(BigDecimal value, FxSourceLoc loc) {
            return insert(incrIndex(), value, loc);
        }

        @Override
        public FxPOJONode addPOJO(Object value, FxSourceLoc loc) {
            return insertPOJO(incrIndex(), value, loc);
        }
        
        @Override
        public FxLinkProxyNode addLink(FxNodeOuterPath value, FxSourceLoc loc) {
            return insertLink(incrIndex(), value, loc);
        }

        @Override
        public FxNullNode addNull(FxSourceLoc loc) {
            return insertNull(incrIndex(), loc);
        }
    }
    
}
