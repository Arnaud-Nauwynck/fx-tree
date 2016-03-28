package fr.an.fxtree.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

public abstract class FxArrayNode extends FxContainerNode {

    // ------------------------------------------------------------------------

    protected FxArrayNode(FxContainerNode parent, FxChildId childId) {
        super(parent, childId);
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

    public abstract <T extends FxNode> T insert(int index, Class<T> clss);

    protected abstract <T extends FxNode> T onInsert(int index, T node);

    public <T extends FxNode> T add(Class<T> clss) {
        int len = size();
        return insert(len, clss);
    }

    @Override
    public abstract void remove(FxNode child);

    @Override
    public abstract FxNode remove(FxChildId childId);

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
    
    public FxArrayNode insertArray(int index) {
        FxArrayNode res = getNodeFactory().newArray();
        return onInsert(index, res);
    }

    public FxArrayNode addArray() {
        return insertArray(size());
    }

    public FxObjNode insertObj(int index) {
        FxObjNode res = getNodeFactory().newObj();
        return onInsert(index, res);
    }

    public FxObjNode addObj() {
        return insertObj(size());
    }

    public FxTextNode insert(int index, String value) {
        FxTextNode res = getNodeFactory().newText(value);
        return onInsert(index, res);
    }

    public FxTextNode add(String value) {
        return insert(size(), value);
    }

    public FxDoubleNode insert(int index, double value) {
        FxDoubleNode res = getNodeFactory().newDouble(value);
        return onInsert(index, res);
    }

    public FxDoubleNode add(double value) {
        return insert(size(), value);
    }

    public FxIntNode insert(int index, int value) {
        FxIntNode res = getNodeFactory().newInt(value);
        return onInsert(index, res);
    }

    public FxIntNode add(int value) {
        return insert(size(), value);
    }

    public FxLongNode insert(int index, long value) {
        FxLongNode res = getNodeFactory().newLong(value);
        return onInsert(index, res);
    }

    public FxLongNode add(long value) {
        return insert(size(), value);
    }

    public FxBoolNode insert(int index, boolean value) {
        FxBoolNode res = getNodeFactory().newBool(value);
        return onInsert(index, res);
    }

    public FxBoolNode add(boolean value) {
        return insert(size(), value);
    }

    public FxBinaryNode insert(int index, byte[] value) {
        FxBinaryNode res = getNodeFactory().newBinary(value);
        return onInsert(index, res);
    }

    public FxBinaryNode add(byte[] value) {
        return insert(size(), value);
    }

    public FxPOJONode insert(int index, BigInteger value) {
        FxPOJONode res = getNodeFactory().newPOJO(value); // use POJO?
        return onInsert(index, res);
    }

    public FxPOJONode add(BigInteger value) {
        return insert(size(), value);
    }

    public FxPOJONode insert(int index, BigDecimal value) {
        FxPOJONode res = getNodeFactory().newPOJO(value); // use POJO?
        return onInsert(index, res);
    }

    public FxPOJONode add(BigDecimal value) {
        return insert(size(), value);
    }

    public FxPOJONode insertPOJO(int index, Object value) {
        FxPOJONode res = getNodeFactory().newPOJO(value);
        return onInsert(index, res);
    }

    public FxPOJONode addPOJO(Object value) {
        return insertPOJO(size(), value);
    }

    public FxNullNode insertNull(int index) {
        FxNullNode res = getNodeFactory().newNull();
        return onInsert(index, res);
    }

    public FxNullNode addNull() {
        return insertNull(size());
    }
    
    // ------------------------------------------------------------------------
    
//    @Override
//    public boolean equals(Object o) {
//        if (o == this) return true;
//        if (o == null) return false;
//        if (o instanceof FxArrayNode) {
//            return children().equals(((FxArrayNode) o).children());
//        }
//        return false;
//    }
//
//    protected boolean _childrenEqual(FxArrayNode other) {
//        return children().equals(other.children());
//    }
//
//    @Override
//    public int hashCode() {
//        return children().hashCode();
//    }

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
        public boolean canAddMoveFrom(FxRootDocument otherParentSrc) {
            return getNodeFactory() == otherParentSrc.getNodeFactory();
        }
        
        @Override
        public FxNode addMoveFrom(FxRootDocument otherParentSrc) {
            FxNode contentSrc = otherParentSrc.getContent();
            otherParentSrc.remove(contentSrc);
            onInsert(incrIndex(), contentSrc);
            return contentSrc;
        }

        @Override
        public FxArrayNode addArray() {
            return insertArray(incrIndex());
        }

        @Override
        public FxObjNode addObj() {
            return insertObj(incrIndex());
        }

        @Override
        public FxTextNode add(String value) {
            return insert(incrIndex(), value);
        }

        @Override
        public FxDoubleNode add(double value) {
            return insert(incrIndex(), value);
        }

        @Override
        public FxIntNode add(int value) {
            return insert(incrIndex(), value);
        }

        @Override
        public FxLongNode add(long value) {
            return insert(incrIndex(), value);
        }

        @Override
        public FxBoolNode add(boolean value) {
            return insert(incrIndex(), value);
        }

        @Override
        public FxBinaryNode add(byte[] value) {
            return insert(incrIndex(), value);
        }

        @Override
        public FxPOJONode add(BigInteger value) {
            return insert(incrIndex(), value);
        }

        @Override
        public FxPOJONode add(BigDecimal value) {
            return insert(incrIndex(), value);
        }

        @Override
        public FxPOJONode addPOJO(Object value) {
            return insertPOJO(incrIndex(), value);
        }

        @Override
        public FxNullNode addNull() {
            return insertNull(incrIndex());
        }
    }
    
}
