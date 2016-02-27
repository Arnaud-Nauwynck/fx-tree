package fr.an.fxtree.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class FxRootDocument extends FxContainerNode {

    private FxNodeFactoryRegistry nodeFactory;
    
    private FxNode childContent;
    
    private Map<String,Object> extraParams = new HashMap<String,Object>();
    
    // ------------------------------------------------------------------------
    
    protected FxRootDocument(FxNodeFactoryRegistry nodeFactory) {
        super(null, null);
        if (nodeFactory == null) throw new IllegalArgumentException();
        this.rootDocument = this;
        this.nodeFactory = nodeFactory;
    }

    // ------------------------------------------------------------------------

    @Override
    public FxNodeType getNodeType() {
        return FxNodeType.ROOT; // cf also  (childContent==null)? FxNodeType.NULL : childContent.getNodeType()  
    }

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

    public FxObjNode getContentObj() {
        return (FxObjNode) childContent;
    }

    public FxArrayNode getContentArray() {
        return (FxArrayNode) childContent;
    }

    @Override
    public int size() {
        return childContent != null? 1 : 0;
    }

    @Override
    public boolean isEmpty() {
        return childContent == null;
    }

    @Override
    public Collection<FxNode> children() {
        return childContent != null? Collections.singleton(childContent) : Collections.emptyList();
    }

    @Override
    public Iterator<FxNode> childIterator() {
        return childContent != null? Collections.singleton(childContent).iterator() : Collections.<FxNode>emptyList().iterator();
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
    
    public FxChildWriter contentWriter() {
        return new RootChildWriter();
    }
    
    public FxObjNode setContentObj() {
        FxObjNode res = nodeFactory.newObj();
        setContent(res);
        return res;
    }

    public FxArrayNode setContentArray() {
        FxArrayNode res = nodeFactory.newArray();
        setContent(res);
        return res;
    }

    public void setContent(FxNode node) {
        if (node == childContent) return;
        addContent(node);
    }

    protected <T extends FxNode> T addContent(T node) {
        if (childContent != null) {
            remove(childContent);
        }
        this.childContent = node;
        if (childContent != null) {
            node._setParent(this, null);
        }
        return node;
    }
    
    public Object getExtraParam(String key) {
        return extraParams.get(key);
    }

    public Object putExtraParam(String key, Object value) {
        return extraParams.put(key, value);
    }

    // internal
    // ------------------------------------------------------------------------

    private final class RootChildWriter extends FxChildWriter {
        
        public RootChildWriter() {
        }

        @Override
        public FxArrayNode addArray() {
            return setContentArray();
        }

        @Override
        public FxObjNode addObj() {
            return setContentObj();
        }

        @Override
        public FxTextNode add(String value) {
            return addContent(nodeFactory.newText(value));
        }

        @Override
        public FxDoubleNode add(double value) {
            return addContent(nodeFactory.newDouble(value));
        }

        @Override
        public FxIntNode add(int value) {
            return addContent(nodeFactory.newInt(value));
        }

        @Override
        public FxBoolNode add(boolean value) {
            return addContent(nodeFactory.newBool(value));
        }

        @Override
        public FxBinaryNode add(byte[] value) {
            return addContent(nodeFactory.newBinary(value));
        }

        @Override
        public FxPOJONode add(BigInteger value) {
            return addContent(nodeFactory.newPOJO(value)); // TODO use POJO
        }

        @Override
        public FxPOJONode add(BigDecimal value) {
            return addContent(nodeFactory.newPOJO(value)); // TODO use POJO
        }

        @Override
        public FxPOJONode addPOJO(Object value) {
            return addContent(nodeFactory.newPOJO(value));
        }

        @Override
        public FxNullNode addNull() {
            return addContent(nodeFactory.newNull());
        }
    }

}

