package fr.an.fxtree.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.path.FxNodeOuterPath;

public abstract class FxRootDocument extends FxContainerNode {

    private static final Logger log = LoggerFactory.getLogger(FxRootDocument.class);

    private FxNode childContent;
    
    private Map<String,Object> extraParams = new HashMap<String,Object>();
    
    private FxSourceLoc location;
    
    // ------------------------------------------------------------------------
    
    protected FxRootDocument(FxNodeFactoryRegistry nodeFactory, FxSourceLoc location) {
        super(null, null, null);
        if (nodeFactory == null) {
            throw new IllegalArgumentException();
        }
        if (location == null) {
            log.warn("sourceLocation not set"); 
        }
        this.nodeFactory = nodeFactory;
        this.location = location;
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
    
    public FxSourceLoc getLocation() {
        return location;
    }

    public void setLocation(FxSourceLoc location) {
        this.location = location;
    }

    @Override
    public int size() {
        return childContent != null? 1 : 0;
    }

    @Override
    public boolean isEmpty() {
        return childContent == null;
    }

    // @Override
    public Collection<FxNode> children() {
        return childContent != null? Collections.singleton(childContent) : Collections.emptyList();
    }

    @Override
    public Iterator<FxNode> childIterator() {
        return childContent != null? Collections.singleton(childContent).iterator() : Collections.<FxNode>emptyList().iterator();
    }
    
    public FxChildWriter contentWriter() {
        return new RootChildWriter();
    }
    
    public FxObjNode setContentObj(FxSourceLoc loc) {
        FxObjNode res = nodeFactory.newObj(loc);
        setContent(res);
        return res;
    }

    public FxArrayNode setContentArray(FxSourceLoc loc) {
        FxArrayNode res = nodeFactory.newArray(loc);
        setContent(res);
        return res;
    }

    public FxTextNode setContentText(String text, FxSourceLoc loc) {
        FxTextNode res = nodeFactory.newText(text, loc);
        setContent(res);
        return res;
    }
    
    public void setContent(FxNode node) {
        if (node == childContent) return;
        addContent(node);
    }

    protected <T extends FxNode> T addContent(T node) {
        if (childContent != null) {
            childContent._setParent(null, null);
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
        protected FxSourceLoc insertLoc() {
            return FxRootDocument.this.getSourceLoc();
        }

        @Override
        public void remove() {
            setContent(null);
        }
        
        @Override
        public FxNode getResultChild() {
            return getContent();
        }

        @Override
        public FxArrayNode addArray(FxSourceLoc loc) {
            return setContentArray(loc);
        }

        @Override
        public FxObjNode addObj(FxSourceLoc loc) {
            return setContentObj(loc);
        }

        @Override
        public FxTextNode add(String value, FxSourceLoc loc) {
            return addContent(nodeFactory.newText(value, loc));
        }

        @Override
        public FxDoubleNode add(double value, FxSourceLoc loc) {
            return addContent(nodeFactory.newDouble(value, loc));
        }

        @Override
        public FxIntNode add(int value, FxSourceLoc loc) {
            return addContent(nodeFactory.newInt(value, loc));
        }

        @Override
        public FxLongNode add(long value, FxSourceLoc loc) {
            return addContent(nodeFactory.newLong(value, loc));
        }

        @Override
        public FxBoolNode add(boolean value, FxSourceLoc loc) {
            return addContent(nodeFactory.newBool(value, loc));
        }

        @Override
        public FxBinaryNode add(byte[] value, FxSourceLoc loc) {
            return addContent(nodeFactory.newBinary(value, loc));
        }

        @Override
        public FxPOJONode add(BigInteger value, FxSourceLoc loc) {
            return addContent(nodeFactory.newPOJO(value, loc)); // TODO use POJO
        }

        @Override
        public FxPOJONode add(BigDecimal value, FxSourceLoc loc) {
            return addContent(nodeFactory.newPOJO(value, loc)); // TODO use POJO
        }

        @Override
        public FxPOJONode addPOJO(Object value, FxSourceLoc loc) {
            return addContent(nodeFactory.newPOJO(value, loc));
        }

        @Override
        public FxLinkProxyNode addLink(FxNodeOuterPath value, FxSourceLoc loc) {
            return addContent(nodeFactory.newLink(value, loc));
        }

        @Override
        public FxNullNode addNull(FxSourceLoc loc) {
            return addContent(nodeFactory.newNull(loc));
        }
    }

}

