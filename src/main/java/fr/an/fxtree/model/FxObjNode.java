package fr.an.fxtree.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.io.CharTypes;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.path.FxNodeOuterPath;

public abstract class FxObjNode extends FxContainerNode {

    // ------------------------------------------------------------------------

    protected FxObjNode(FxContainerNode parent, FxChildId childId, FxSourceLoc sourceLoc) {
        super(parent, childId, sourceLoc);
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

    public abstract Iterator<Map.Entry<String, FxNode>> fields();

    public Map<String, FxNode> fieldsMap() {
        Map<String, FxNode> res = new LinkedHashMap<>();
        for(Iterator<Map.Entry<String, FxNode>> iter = fields(); iter.hasNext(); ) {
            Entry<String, FxNode> e = iter.next();
            res.put(e.getKey(), e.getValue());
        }
        return res;
    }

    public Iterator<Map.Entry<String, FxNode>> fieldsIterCopy(boolean useCopy) {
        return (useCopy)? fieldsMap().entrySet().iterator() : fields();
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

    protected abstract <T extends FxNode> T onPut(String name, T node);

    protected <T extends FxNode> T putNode(String name, T node, FxSourceLoc loc) {
        if (loc == null) {
            loc = getSourceLoc();
        }
        node.setSourceLoc(loc);
        return onPut(name, node);
    }

    public abstract <T extends FxNode> T get(String name);

    public abstract FxNode remove(FxNode child);

    public abstract FxNode remove(FxChildId childId);

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
        return new ObjChildWriter(this, name, false);
    }

    public FxChildWriter putBuilder(String name, boolean useIncrSuffix) {
        return new ObjChildWriter(this, name, useIncrSuffix);
    }

    public FxArrayNode putArray(String name, FxSourceLoc loc) {
        FxArrayNode res = getNodeFactory().newArray(loc);
        return onPut(name, res);
    }

    public FxObjNode putObj(String name, FxSourceLoc loc) {
        FxObjNode res = getNodeFactory().newObj(loc);
        return onPut(name, res);
    }

    public FxTextNode put(String name, String value, FxSourceLoc loc) {
        FxTextNode res = getNodeFactory().newText(value, loc);
        return onPut(name, res);
    }

    public FxDoubleNode put(String name, double value, FxSourceLoc loc) {
        FxDoubleNode res = getNodeFactory().newDouble(value, loc);
        return onPut(name, res);
    }

    public FxIntNode put(String name, int value, FxSourceLoc loc) {
        FxIntNode res = getNodeFactory().newInt(value, loc);
        return onPut(name, res);
    }

    public FxLongNode put(String name, long value, FxSourceLoc loc) {
        FxLongNode res = getNodeFactory().newLong(value, loc);
        return onPut(name, res);
    }

    public FxBoolNode put(String name, boolean value, FxSourceLoc loc) {
        FxBoolNode res = getNodeFactory().newBool(value, loc);
        return onPut(name, res);
    }

    public FxBinaryNode put(String name, byte[] value, FxSourceLoc loc) {
        FxBinaryNode res = getNodeFactory().newBinary(value, loc);
        return onPut(name, res);
    }

    public FxPOJONode put(String name, BigInteger value, FxSourceLoc loc) {
        FxPOJONode res = getNodeFactory().newPOJO(value, loc); // TODO use POJO?
        return onPut(name, res);
    }

    public FxPOJONode put(String name, BigDecimal value, FxSourceLoc loc) {
        FxPOJONode res = getNodeFactory().newPOJO(value, loc); // TODO use POJO?
        return onPut(name, res);
    }

    public FxPOJONode putPOJO(String name, Object value, FxSourceLoc loc) {
        FxPOJONode res = getNodeFactory().newPOJO(value, loc);
        return onPut(name, res);
    }

    public FxLinkProxyNode putLink(String name, FxNodeOuterPath value, FxSourceLoc loc) {
        FxLinkProxyNode res = getNodeFactory().newLink(value, loc);
        return onPut(name, res);
    }

    public FxNullNode putNull(String name, FxSourceLoc loc) {
        FxNullNode res = getNodeFactory().newNull(loc);
        return onPut(name, res);
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
        if (o instanceof FxObjNode) {
            return _childrenEqual((FxObjNode) o);
        }
        return false;
    }

    protected boolean _childrenEqual(FxObjNode other) {
        return fieldsMap().equals(other.fieldsMap());
    }

    @Override
    public int hashCode() {
        return fieldsMap().hashCode();
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


    public static final class ObjChildWriter extends FxChildWriter {
        
        private FxObjNode dest;
        private String baseName;
        private boolean useIncrSuffix;
        private int currIndex;
        
        public ObjChildWriter(FxObjNode dest, String name, boolean useIncrSuffix) {
            this.dest = dest;
            this.baseName = name;
            this.useIncrSuffix = useIncrSuffix;
        }

        @Override
        protected FxSourceLoc insertLoc() {
            return dest.getSourceLoc();
        }

        private String incrName() {
            if (!useIncrSuffix) {
                return baseName;
            } else {
                String res = (currIndex == 0)? baseName : baseName + currIndex;
                currIndex++;
                return res;
            }
        }
        
        private String currChildName() {
            if (!useIncrSuffix) {
                return baseName;
            } else {
                return (currIndex == 0)? baseName : baseName + currIndex;
            }
        }
        
        @Override
        public void remove() {
            dest.remove(currChildName());
        }

        @Override
        public FxNode getResultChild() {
            return dest.get(currChildName());
        }
        
        @Override
        public FxArrayNode addArray(FxSourceLoc loc) {
            return dest.putArray(incrName(), loc);
        }

        @Override
        public FxObjNode addObj(FxSourceLoc loc) {
            return dest.putObj(incrName(), loc);
        }

        @Override
        public FxTextNode add(String value, FxSourceLoc loc) {
            return dest.put(incrName(), value, loc);
        }

        @Override
        public FxDoubleNode add(double value, FxSourceLoc loc) {
            return dest.put(incrName(), value, loc);
        }

        @Override
        public FxIntNode add(int value, FxSourceLoc loc) {
            return dest.put(incrName(), value, loc);
        }

        @Override
        public FxLongNode add(long value, FxSourceLoc loc) {
            return dest.put(incrName(), value, loc);
        }

        @Override
        public FxBoolNode add(boolean value, FxSourceLoc loc) {
            return dest.put(incrName(), value, loc);
        }

        @Override
        public FxBinaryNode add(byte[] value, FxSourceLoc loc) {
            return dest.put(incrName(), value, loc);
        }

        @Override
        public FxPOJONode add(BigInteger value, FxSourceLoc loc) {
            return dest.put(incrName(), value, loc);
        }

        @Override
        public FxPOJONode add(BigDecimal value, FxSourceLoc loc) {
            return dest.put(incrName(), value, loc);
        }

        @Override
        public FxPOJONode addPOJO(Object value, FxSourceLoc loc) {
            return dest.putPOJO(incrName(), value, loc);
        }

        @Override
        public FxLinkProxyNode addLink(FxNodeOuterPath value, FxSourceLoc loc) {
            return dest.putLink(incrName(), value, loc);
        }

        @Override
        public FxNullNode addNull(FxSourceLoc loc) {
            return dest.putNull(incrName(), loc);
        }
    }
    
}
