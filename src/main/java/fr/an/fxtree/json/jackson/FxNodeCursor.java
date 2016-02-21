package fr.an.fxtree.json.jackson;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;

import fr.an.fxtree.model.FxContainerNode;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

/**
 * similar to jackson NodeCursor, but for FxNode
 * 
 * Helper class used by {@link TreeTraversingParser} to keep track
 * of current location within traversed JSON tree.
 */
public abstract class FxNodeCursor extends JsonStreamContext {
    
    /** Parent cursor of this cursor, if any; null for root cursors. */
    protected final FxNodeCursor _parent;

    protected String _currentName;

    protected java.lang.Object _currentValue;
    
    // ------------------------------------------------------------------------
    
    public FxNodeCursor(int contextType, FxNodeCursor parent) {
        _type = contextType;
        _index = -1;
        _parent = parent;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public final FxNodeCursor getParent() { return _parent; }

    @Override
    public final String getCurrentName() {
        return _currentName;
    }

    public void overrideCurrentName(String name) {
        _currentName = name;
    }

    @Override
    public java.lang.Object getCurrentValue() {
        return _currentValue;
    }

    @Override
    public void setCurrentValue(java.lang.Object v) {
        _currentValue = v;
    }
    
    public abstract JsonToken nextToken();
    public abstract JsonToken nextValue();
    public abstract JsonToken endToken();

    public abstract FxNode currentNode();
    public abstract boolean currentHasChildren();
    
    /**
     * Method called to create a new context for iterating all
     * contents of the current structured value (JSON array or object)
     */
    public final FxNodeCursor iterateChildren() {
        FxNode n = currentNode();
        if (n == null) throw new IllegalStateException("No current node");
        if (n.isArray()) { // false since we have already returned START_ARRAY
            return new FxArrayCursor((FxArrayNode) n, this);
        }
        if (n.isObject()) {
            return new FxObjectCursor((FxObjNode) n, this);
        }
        throw new IllegalStateException("Current node of type "+n.getClass().getName());
    }

    // Concrete implementations
    // ------------------------------------------------------------------------
    
    /**
     * Context matching root-level value nodes (i.e. anything other
     * than JSON Object and Array).
     * Note that context is NOT created for leaf values.
     */
    protected final static class FxRootValueCursor extends FxNodeCursor {
        protected FxNode _node;

        protected boolean _done = false;

        public FxRootValueCursor(FxNode n, FxNodeCursor p) {
            super(JsonStreamContext.TYPE_ROOT, p);
            _node = n;
        }

        @Override
        public void overrideCurrentName(String name) {            
        }
        
        @Override
        public JsonToken nextToken() {
            if (!_done) {
                _done = true;
                return FxNodeAsToken.asToken(_node);
            }
            _node = null;
            return null;
        }
        
        @Override
        public JsonToken nextValue() { return nextToken(); }
        @Override
        public JsonToken endToken() { return null; }
        @Override
        public FxNode currentNode() { return _node; }
        @Override
        public boolean currentHasChildren() { return false; }
    }

    /**
     * Cursor used for traversing non-empty JSON Array nodes
     */
    protected final static class FxArrayCursor extends FxNodeCursor {
        protected Iterator<FxNode> _contents;
        protected FxNode _currentNode;

        public FxArrayCursor(FxArrayNode n, FxNodeCursor p) {
            super(JsonStreamContext.TYPE_ARRAY, p);
            _contents = n.childIterator();
        }

        @Override
        public JsonToken nextToken() {
            if (!_contents.hasNext()) {
                _currentNode = null;
                return null;
            }
            _currentNode = _contents.next();
            return FxNodeAsToken.asToken(_currentNode);
        }

        @Override
        public JsonToken nextValue() { return nextToken(); }
        @Override
        public JsonToken endToken() { return JsonToken.END_ARRAY; }

        @Override
        public FxNode currentNode() { return _currentNode; }
        @Override
        public boolean currentHasChildren() {
            // note: ONLY to be called for container nodes
            return ! ((FxContainerNode) currentNode()).isEmpty();
        }
    }

    /**
     * Cursor used for traversing non-empty JSON Object nodes
     */
    protected final static class FxObjectCursor extends FxNodeCursor {
        protected Iterator<Map.Entry<String, FxNode>> _contents;
        protected Map.Entry<String, FxNode> _current;

        protected boolean _needEntry;
        
        public FxObjectCursor(FxObjNode n, FxNodeCursor parent) {
            super(JsonStreamContext.TYPE_OBJECT, parent);
            _contents = n.fields();
            _needEntry = true;
        }

        @Override
        public JsonToken nextToken()
        {
            // Need a new entry?
            if (_needEntry) {
                if (!_contents.hasNext()) {
                    _currentName = null;
                    _current = null;
                    return null;
                }
                _needEntry = false;
                _current = _contents.next();
                _currentName = (_current == null) ? null : _current.getKey();
                return JsonToken.FIELD_NAME;
            }
            _needEntry = true;
            return FxNodeAsToken.asToken(_current.getValue());
        }

        @Override
        public JsonToken nextValue()
        {
            JsonToken t = nextToken();
            if (t == JsonToken.FIELD_NAME) {
                t = nextToken();
            }
            return t;
        }

        @Override
        public JsonToken endToken() { return JsonToken.END_OBJECT; }

        @Override
        public FxNode currentNode() {
            return (_current == null) ? null : _current.getValue();
        }
        @Override
        public boolean currentHasChildren() {
            // note: ONLY to be called for container nodes
            return ! ((FxContainerNode) currentNode()).isEmpty();
        }
    }
}
