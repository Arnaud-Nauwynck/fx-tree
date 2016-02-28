package fr.an.fxtree.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.io.CharTypes;

public abstract class FxObjNode extends FxContainerNode {

    // ------------------------------------------------------------------------

    protected FxObjNode(FxContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------

    @Override
    public FxNodeType getNodeType() {
        return FxNodeType.OBJECT;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitObj(this);
    }

    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitObj(this, param);
    }

    @Override
    public abstract int size();

    @Override
    public abstract Collection<FxNode> children();

    public abstract Iterator<Map.Entry<String, FxNode>> fields();

    public Map<String, FxNode> fieldsHashMapCopy() {
        Map<String, FxNode> res = new HashMap<>();
        for(Iterator<Map.Entry<String, FxNode>> iter = fields(); iter.hasNext(); ) {
            Entry<String, FxNode> e = iter.next();
            res.put(e.getKey(), e.getValue());
        }
        return res;
    }

    @FunctionalInterface
    public static interface FieldFunc {
        public void onField(String fieldName, FxNode fieldValue);
    }
    
    public void forEachFields(FieldFunc callback) {
        if (!isEmpty()) {
            for(Iterator<Map.Entry<String, FxNode>> iter = fields(); iter.hasNext(); ) {
                Entry<String, FxNode> e = iter.next();
                callback.onField(e.getKey(), e.getValue());
            }
        }
    }

    public abstract <T extends FxNode> T put(String name, Class<T> clss);

    protected abstract <T extends FxNode> T onPut(String name, T node);

    public abstract <T extends FxNode> T get(String name);

    @Override
    public abstract void remove(FxNode child);

    public abstract FxNode remove(String name);

    public abstract void removeAll();

    public void removeAll(Collection<String> childNames) {
        for (String childName : childNames) {
            remove(childName);
        }
    }

    // helper methods for put(String name, Class<T> clss)
    // ------------------------------------------------------------------------

    public FxChildWriter putBuilder(String name) {
        return new ObjChildWriter(name);
    }
    
    public FxArrayNode putArray(String name) {
        FxArrayNode res = getNodeFactory().newArray();
        return onPut(name, res);
    }

    public FxObjNode putObj(String name) {
        FxObjNode res = getNodeFactory().newObj();
        return onPut(name, res);
    }

    public FxTextNode put(String name, String value) {
        FxTextNode res = getNodeFactory().newText(value);
        return onPut(name, res);
    }

    public FxDoubleNode put(String name, double value) {
        FxDoubleNode res = getNodeFactory().newDouble(value);
        return onPut(name, res);
    }

    public FxIntNode put(String name, int value) {
        FxIntNode res = getNodeFactory().newInt(value);
        return onPut(name, res);
    }

    public FxLongNode put(String name, long value) {
        FxLongNode res = getNodeFactory().newLong(value);
        return onPut(name, res);
    }

    public FxBoolNode put(String name, boolean value) {
        FxBoolNode res = getNodeFactory().newBool(value);
        return onPut(name, res);
    }

    public FxBinaryNode put(String name, byte[] value) {
        FxBinaryNode res = getNodeFactory().newBinary(value);
        return onPut(name, res);
    }

    public FxPOJONode put(String name, BigInteger value) {
        FxPOJONode res = getNodeFactory().newPOJO(value); // TODO use POJO?
        return onPut(name, res);
    }

    public FxPOJONode put(String name, BigDecimal value) {
        FxPOJONode res = getNodeFactory().newPOJO(value); // TODO use POJO?
        return onPut(name, res);
    }

    public FxPOJONode putPOJO(String name, Object value) {
        FxPOJONode res = getNodeFactory().newPOJO(value);
        return onPut(name, res);
    }

    public FxNullNode putNull(String name) {
        FxNullNode res = getNodeFactory().newNull();
        return onPut(name, res);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (o instanceof FxObjNode) {
            return _childrenEqual((FxObjNode) o);
        }
        return false;
    }

    protected boolean _childrenEqual(FxObjNode other) {
        return children().equals(other.children());
    }

    @Override
    public int hashCode() {
        return children().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(32 + (size() << 4));
        sb.append("{");
        int count = 0;
        for (Iterator<Map.Entry<String, FxNode>> iter = fields(); iter.hasNext();) {
            Entry<String, FxNode> e = iter.next();
            if (count > 0) {
                sb.append(",");
            }
            ++count;
            appendQuoted(sb, e.getKey());
            sb.append(':');
            sb.append(e.getValue().toString());
        }
        sb.append("}");
        return sb.toString();
    }

    // internal
    // ------------------------------------------------------------------------
    
    protected static void appendQuoted(StringBuilder sb, String content) {
        sb.append('"');
        CharTypes.appendQuoted(sb, content);
        sb.append('"');
    }


    private final class ObjChildWriter extends FxChildWriter {
        
        private String baseName;
        private int currIndex;
        
        public ObjChildWriter(String name) {
            this.baseName = name;
        }

        private String incrName() {
            String res = (currIndex == 0)? baseName : baseName + currIndex;
            currIndex++;
            return res;
        }
        
        @Override
        public FxArrayNode addArray() {
            return putArray(incrName());
        }

        @Override
        public FxObjNode addObj() {
            return putObj(incrName());
        }

        @Override
        public FxTextNode add(String value) {
            return put(incrName(), value);
        }

        @Override
        public FxDoubleNode add(double value) {
            return put(incrName(), value);
        }

        @Override
        public FxIntNode add(int value) {
            return put(incrName(), value);
        }

        @Override
        public FxLongNode add(long value) {
            return put(incrName(), value);
        }

        @Override
        public FxBoolNode add(boolean value) {
            return put(incrName(), value);
        }

        @Override
        public FxBinaryNode add(byte[] value) {
            return put(incrName(), value);
        }

        @Override
        public FxPOJONode add(BigInteger value) {
            return put(incrName(), value);
        }

        @Override
        public FxPOJONode add(BigDecimal value) {
            return put(incrName(), value);
        }

        @Override
        public FxPOJONode addPOJO(Object value) {
            return putPOJO(incrName(), value);
        }

        @Override
        public FxNullNode addNull() {
            return putNull(incrName());
        }
    }
    
}
