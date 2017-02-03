package fr.an.fxtree.format.json.jackson;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.ParserMinimalBase;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.FxTextNode;

/**
 * equivalent to jackson TreeTraversingParser, but using FxNode tree
 */
public class FxNodeTreeTraversingParser extends ParserMinimalBase {

    protected ObjectCodec _objectCodec;

    protected FxNodeCursor _nodeCursor;

    /**
     * Sometimes parser needs to buffer a single look-ahead token; if so, it'll
     * be stored here. This is currently used for handling
     */
    protected JsonToken _nextToken;

    /**
     * Flag needed to handle recursion into contents of child Array/Object
     * nodes.
     */
    protected boolean _startContainer;

    /**
     * Flag that indicates whether parser is closed or not. Gets set when parser
     * is either closed by explicit call ({@link #close}) or when end-of-input
     * is reached.
     */
    protected boolean _closed;

    // ------------------------------------------------------------------------

    public FxNodeTreeTraversingParser(FxNode n) {
        this(n, null);
    }

    public FxNodeTreeTraversingParser(FxNode n, ObjectCodec codec) {
        super(0);
        this._objectCodec = codec;
        if (n.isArray()) {
            _nextToken = JsonToken.START_ARRAY;
            _nodeCursor = new FxNodeCursor.FxArrayCursor((FxArrayNode) n, null);
        } else if (n.isObject()) {
            _nextToken = JsonToken.START_OBJECT;
            _nodeCursor = new FxNodeCursor.FxObjectCursor((FxObjNode) n, null);
        } else { // value node
            _nodeCursor = new FxNodeCursor.FxRootValueCursor(n, null);
        }
    }

    @Override
    public void close() throws IOException {
        if (!_closed) {
            _closed = true;
            _nodeCursor = null;
            _currToken = null;
        }
    }

    @Override
    public boolean isClosed() {
        return _closed;
    }

    // ------------------------------------------------------------------------

    @Override
    public void setCodec(ObjectCodec c) {
        _objectCodec = c;
    }

    @Override
    public ObjectCodec getCodec() {
        return _objectCodec;
    }

    @Override
    public Version version() {
        return com.fasterxml.jackson.databind.cfg.PackageVersion.VERSION;
    }

    // ------------------------------------------------------------------------

    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
        if (_nextToken != null) {
            _currToken = _nextToken;
            _nextToken = null;
            return _currToken;
        }
        // are we to descend to a container child?
        if (_startContainer) {
            _startContainer = false;
            // minor optimization: empty containers can be skipped
            if (!_nodeCursor.currentHasChildren()) {
                _currToken = (_currToken == JsonToken.START_OBJECT) ?
                    JsonToken.END_OBJECT : JsonToken.END_ARRAY;
                return _currToken;
            }
            _nodeCursor = _nodeCursor.iterateChildren();
            _currToken = _nodeCursor.nextToken();
            if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
                _startContainer = true;
            }
            return _currToken;
        }
        // No more content?
        if (_nodeCursor == null) {
            _closed = true; // if not already set
            return null;
        }
        // Otherwise, next entry from current cursor
        _currToken = _nodeCursor.nextToken();
        if (_currToken != null) {
            if (_currToken == JsonToken.START_OBJECT || _currToken == JsonToken.START_ARRAY) {
                _startContainer = true;
            }
            return _currToken;
        }
        // null means no more children; need to return end marker
        _currToken = _nodeCursor.endToken();
        _nodeCursor = _nodeCursor.getParent();
        return _currToken;
    }

    @Override
    public JsonParser skipChildren() throws IOException, JsonParseException {
        if (_currToken == JsonToken.START_OBJECT) {
            _startContainer = false;
            _currToken = JsonToken.END_OBJECT;
        } else if (_currToken == JsonToken.START_ARRAY) {
            _startContainer = false;
            _currToken = JsonToken.END_ARRAY;
        }
        return this;
    }


    // ------------------------------------------------------------------------

    @Override
    public String getCurrentName() {
        return (_nodeCursor == null) ? null : _nodeCursor.getCurrentName();
    }

    @Override
    public void overrideCurrentName(String name) {
        if (_nodeCursor != null) {
            _nodeCursor.overrideCurrentName(name);
        }
    }

    @Override
    public JsonStreamContext getParsingContext() {
        return _nodeCursor;
    }

    @Override
    public JsonLocation getTokenLocation() {
        return JsonLocation.NA;
    }

    @Override
    public JsonLocation getCurrentLocation() {
        return JsonLocation.NA;
    }

    // access to textual content
    // ------------------------------------------------------------------------

    @Override
    public String getText() {
        if (_closed) {
            return null;
        }
        // need to separate handling a bit...
        switch (_currToken) {
        case FIELD_NAME:
            return _nodeCursor.getCurrentName();
        case VALUE_STRING:
            return ((FxTextNode) currentNode()).getValue();
        case VALUE_NUMBER_INT:
        case VALUE_NUMBER_FLOAT:
            return String.valueOf(currentNode().numberValue());
        case VALUE_EMBEDDED_OBJECT:
            FxNode n = currentNode();
            if (n != null && n.isBinary()) {
                // this will convert it to base64
                return n.asText();
            }
        default:
            return (_currToken == null) ? null : _currToken.asString();
        }
    }

    @Override
    public char[] getTextCharacters() throws IOException, JsonParseException {
        return getText().toCharArray();
    }

    @Override
    public int getTextLength() throws IOException, JsonParseException {
        return getText().length();
    }

    @Override
    public int getTextOffset() throws IOException, JsonParseException {
        return 0;
    }

    @Override
    public boolean hasTextCharacters() {
        // generally we do not have efficient access as char[], hence:
        return false;
    }

    // typed non-text access
    // ------------------------------------------------------------------------

    @Override
    public NumberType getNumberType() throws IOException, JsonParseException {
        FxNode n = currentNumericNode();
        if (n == null) return null;
        return (n == null) ? null : Fx2JacksonUtils.numberType2Jackson(n.numberType());
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
        return currentNumericNode().bigIntegerValue();
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException, JsonParseException {
        return currentNumericNode().decimalValue();
    }

    @Override
    public double getDoubleValue() throws IOException, JsonParseException {
        return currentNumericNode().doubleValue();
    }

    @Override
    public float getFloatValue() throws IOException, JsonParseException {
        return (float) currentNumericNode().doubleValue();
    }

    @Override
    public long getLongValue() throws IOException, JsonParseException {
        return currentNumericNode().longValue();
    }

    @Override
    public int getIntValue() throws IOException, JsonParseException {
        return currentNumericNode().intValue();
    }

    @Override
    public Number getNumberValue() throws IOException, JsonParseException {
        return currentNumericNode().numberValue();
    }

    @Override
    public Object getEmbeddedObject() {
        if (!_closed) {
            FxNode n = currentNode();
            if (n != null) {
                if (n.isPojo()) {
                    return ((FxPOJONode) n).getValue();
                }
                if (n.isBinary()) {
                    return ((FxBinaryNode) n).binaryValue();
                }
            }
        }
        return null;
    }

    /*
     * /********************************************************** /* Public
     * API, typed binary (base64) access
     * /**********************************************************
     */

    @Override
    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException, JsonParseException {
        // Multiple possibilities...
        FxNode n = currentNode();
        if (n != null) { // binary node?
            byte[] data = n.binaryValue();
            // (or TextNode, which can also convert automatically!)
            if (data != null) {
                return data;
            }
            // Or maybe byte[] as POJO?
            if (n.isPojo()) {
                Object ob = ((FxPOJONode) n).getValue();
                if (ob instanceof byte[]) {
                    return (byte[]) ob;
                }
            }
        }
        // otherwise return null to mark we have no binary content
        return null;
    }

    @Override
    public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException, JsonParseException {
        byte[] data = getBinaryValue(b64variant);
        if (data != null) {
            out.write(data, 0, data.length);
            return data.length;
        }
        return 0;
    }

    // internal
    // ------------------------------------------------------------------------


    protected FxNode currentNode() {
        if (_closed || _nodeCursor == null) {
            return null;
        }
        return _nodeCursor.currentNode();
    }

    protected FxNode currentNumericNode() throws JsonParseException {
        FxNode n = currentNode();
        if (n == null || !n.isNumber()) {
            JsonToken t = (n == null) ? null : FxNodeAsToken.asToken(n);
            throw _constructError("Current token (" + t + ") not numeric, can not use numeric value accessors");
        }
        return n;
    }

    @Override
    protected void _handleEOF() throws JsonParseException {
        _throwInternal(); // should never get called
    }
}
