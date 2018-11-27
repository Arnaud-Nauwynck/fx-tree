package fr.an.fxtree.format.json.jackson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TextNode;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxBoolNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxNumberType;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.FxTextNode;
import fr.an.fxtree.model.FxValueNode;

public final class Fx2JacksonUtils {

    /** private to force all static */
    private Fx2JacksonUtils() {
    }

    public static FxNode jsonNodeToFxTree(JsonNode src, FxSourceLoc loc) {
        if (src == null) {
            return null;
        }
        FxMemRootDocument doc = new FxMemRootDocument(loc);
        jsonNodeToFxTree(doc.contentWriter(), src, loc);
        return doc.getContent();
    }

    public static void jsonNodesToFxTrees(FxChildWriter dest, Collection<JsonNode> src, FxSourceLoc source) {
        if (src != null && !src.isEmpty()) {
            for(JsonNode e : src) {
                jsonNodeToFxTree(dest, e, source);
            }
        }
    }
    
    public static FxNode jsonNodeToFxTree(FxChildWriter dest, JsonNode src, FxSourceLoc loc) {
        switch (src.getNodeType()) {
        case OBJECT:
            FxObjNode destObj = dest.addObj(loc);
            fillJsonObjToFxObj(destObj, (ObjectNode) src, loc);
            return destObj;
        case ARRAY:
            FxArrayNode destArray = dest.addArray(loc);
            fillJsonArrayToFxArray(destArray, (ArrayNode) src, loc);
            return destArray;
        case BINARY:
            return dest.add(((BinaryNode) src).binaryValue(), loc);
        case BOOLEAN:
            return dest.add(((BooleanNode) src).booleanValue(), loc);
        case MISSING:
            //?? use null
            return null;
        case NULL:
            return dest.addNull(loc);
        case NUMBER:
            NumericNode srcNumber = (NumericNode) src;
            JsonParser.NumberType numberType = src.numberType();
            switch(numberType) {
            case INT:
               return dest.add(srcNumber.intValue(), loc);
            case LONG:
                return dest.add(srcNumber.longValue(), loc);
            case BIG_INTEGER:
                return dest.addPOJO(srcNumber.bigIntegerValue(), loc); // use POJO for jackson BigInteger
            case FLOAT:
                return dest.add(srcNumber.floatValue(), loc); // use Double for Jackson Float
            case DOUBLE:
                return dest.add(srcNumber.doubleValue(), loc);
            case BIG_DECIMAL:
                return dest.addPOJO(srcNumber.decimalValue(), loc); // use POJO for jackson BigDecimal
            default:
                throw new RuntimeException();
            }
        case POJO:
            return dest.addPOJO(((POJONode) src).getPojo(), loc);
        case STRING:
            return dest.add(((TextNode) src).textValue(), loc);
        default:
            throw new RuntimeException();
        }
    }
    
    public static void fillJsonArrayToFxArray(FxArrayNode dest, ArrayNode src, FxSourceLoc loc) {
        if (! dest.isEmpty()) {
            dest.removeAll();
        }
        final int len = src.size();
        for (int i = 0; i < len; i++) {
            JsonNode srcElt = src.get(i);
            switch (srcElt.getNodeType()) {
            case ARRAY:
                FxArrayNode eltArray = dest.addArray(loc);
                fillJsonArrayToFxArray(eltArray, (ArrayNode) srcElt, loc);
                break;
            case OBJECT:
                FxObjNode eltObj = dest.addObj(loc);
                fillJsonObjToFxObj(eltObj, (ObjectNode) srcElt, loc);
                break;
            case BINARY:
                dest.add(((BinaryNode) srcElt).binaryValue(), loc);
                break;
            case BOOLEAN:
                dest.add(((BooleanNode) srcElt).booleanValue(), loc);
                break;
            case MISSING:
                //?? skip / use null
                break;
            case NULL:
                dest.addNull(loc);
                break;
            case NUMBER:
                NumericNode srcNumber = (NumericNode) srcElt;
                JsonParser.NumberType numberType = srcElt.numberType();
                switch(numberType) {
                case INT:
                    dest.add(srcNumber.intValue(), loc);
                    break;
                case LONG:
                   dest.add(srcNumber.longValue(), loc);
                    break;
                case BIG_INTEGER:
                    dest.addPOJO(srcNumber.bigIntegerValue(), loc); // use POJO for jackson BigInteger
                    break;
                case FLOAT:
                    dest.add(srcNumber.floatValue(), loc); // use Double for Jackson Float
                    break;
                case DOUBLE:
                    dest.add(srcNumber.doubleValue(), loc);
                    break;
                case BIG_DECIMAL:
                    dest.addPOJO(srcNumber.decimalValue(), loc); // use POJO for jackson BigDecimal
                    break;
                }
                break;
            case POJO:
                dest.addPOJO(((POJONode) srcElt).getPojo(), loc);
                break;
            case STRING:
                dest.add(((TextNode) srcElt).textValue(), loc);
                break;
            default:
            throw new RuntimeException();
            }
        }
    }
    
    
    public static void fillJsonObjToFxObj(FxObjNode dest, ObjectNode src, FxSourceLoc loc) {
        if (! dest.isEmpty()) {
            dest.removeAll();
        }
        for (Iterator<Entry<String, JsonNode>> iter = src.fields(); iter.hasNext(); ) {
            Entry<String, JsonNode> e = iter.next();
            String field = e.getKey();
            JsonNode srcElt = e.getValue();
            switch (srcElt.getNodeType()) {
            case ARRAY:
                FxArrayNode eltArray = dest.putArray(field, loc);
                fillJsonArrayToFxArray(eltArray, (ArrayNode) srcElt, loc);
                break;
            case OBJECT:
                FxObjNode eltObj = dest.putObj(field, loc);
                fillJsonObjToFxObj(eltObj, (ObjectNode) srcElt, loc);
                break;
            case BINARY:
                dest.put(field, ((BinaryNode) srcElt).binaryValue(), loc);
                break;
            case BOOLEAN:
                dest.put(field, ((BooleanNode) srcElt).booleanValue(), loc);
                break;
            case MISSING:
                //?? skip / use null
                break;
            case NULL:
                dest.putNull(field, loc);
                break;
            case NUMBER:
                NumericNode srcNumber = (NumericNode) srcElt;
                JsonParser.NumberType numberType = srcElt.numberType();
                switch(numberType) {
               case INT:
                    dest.put(field, srcNumber.intValue(), loc);
                    break;
                case LONG:
                    dest.put(field, srcNumber.longValue(), loc);
                    break;
                case BIG_INTEGER:
                    dest.putPOJO(field, srcNumber.bigIntegerValue(), loc); // use POJO for jackson BigInteger
                    break;
                case FLOAT:
                    dest.put(field, srcNumber.floatValue(), loc); // use Double for Jackson Float
                    break;
                case DOUBLE:
                    dest.put(field, srcNumber.doubleValue(), loc);
                    break;
                case BIG_DECIMAL:
                    dest.putPOJO(field, srcNumber.decimalValue(), loc); // use POJO for jackson BigDecimal
                    break;
                }
                break;
            case POJO:
                dest.putPOJO(field, ((POJONode) srcElt).getPojo(), loc);
                break;
            case STRING:
                dest.put(field, ((TextNode) srcElt).textValue(), loc);
                break;
            default:
            throw new RuntimeException();
            }
        }
    }

    // conversion FxNode -> Jackson JsonNode
    // ------------------------------------------------------------------------

    public static JsonNode fxTreeToJsonNode(FxNode src) {
        return fxTreeToJsonNode(src, JsonNodeFactory.instance);
    }
    
    public static JsonNode fxTreeToJsonNode(FxNode src, JsonNodeFactory jsonNodeFactory) {
        if (src == null) {
            return null;
        }
        JsonNode res;
        switch (src.getNodeType()) {
            case ARRAY:
                ArrayNode destArray = new ArrayNode(jsonNodeFactory);
                fillFxArrayToJsonArray(destArray, (FxArrayNode) src);
                res = destArray;
                break;
            case OBJECT:
                ObjectNode destObj = new ObjectNode(jsonNodeFactory);
                fillFxObjToJsonObj(destObj, (FxObjNode) src);
                res = destObj;
                break;
            case BINARY:
                res = new BinaryNode(((FxBinaryNode) src).binaryValue());
                break;
            case BOOLEAN:
                res = BooleanNode.valueOf(((FxBoolNode) src).booleanValue());
                break;
            case MISSING:
                //?? skip / use null
                res = null;
                break;
            case NULL:
                res = NullNode.getInstance();
                break;
            case NUMBER:
                FxValueNode srcNumber = (FxValueNode) src;
                FxNumberType numberType = src.numberType();
                switch(numberType) {
                case INT:
                    res = IntNode.valueOf(srcNumber.intValue());
                    break;
                case LONG:
                    res = LongNode.valueOf(srcNumber.longValue());
                    break;
                case BIG_INTEGER:
                    res = BigIntegerNode.valueOf(srcNumber.bigIntegerValue());
                    break;
                case FLOAT:
                    res = FloatNode.valueOf(srcNumber.floatValue());
                    break;
                case DOUBLE:
                    res = DoubleNode.valueOf(srcNumber.doubleValue());
                    break;
                case BIG_DECIMAL:
                    res = DecimalNode.valueOf(srcNumber.decimalValue());
                    break;
                default:                
                    throw new UnsupportedOperationException();
                }
                break;
            case POJO:
                res = new POJONode(((FxPOJONode) src).getValue());
                break;
           case STRING:
                res = TextNode.valueOf(((FxTextNode) src).textValue());
                break;
            default:                
                throw new UnsupportedOperationException();
        }
        return res;
    }
        
    public static void fillFxArrayToJsonArray(ArrayNode dest, FxArrayNode src) {
//        if (! dest.isEmpty()) {
//            dest.removeAl();
//        }
        final int len = src.size();
        for (int i = 0; i < len; i++) {
            FxNode srcElt = src.get(i);
            switch (srcElt.getNodeType()) {
            case ARRAY:
                ArrayNode eltArray = dest.addArray();
                fillFxArrayToJsonArray(eltArray, (FxArrayNode) srcElt);
                break;
            case OBJECT:
                ObjectNode eltObj = dest.addObject();
                fillFxObjToJsonObj(eltObj, (FxObjNode) srcElt);
                break;
            case BINARY:
                dest.add(((FxBinaryNode) srcElt).binaryValue());
                break;
            case BOOLEAN:
                dest.add(((FxBoolNode) srcElt).booleanValue());
                break;
            case MISSING:
                //?? skip / use null
                break;
            case NULL:
                dest.addNull();
                break;
            case NUMBER:
                FxValueNode srcNumber = (FxValueNode) srcElt;
                FxNumberType numberType = srcElt.numberType();
                switch(numberType) {
                case INT:
                    dest.add(srcNumber.intValue());
                    break;
                case LONG:
                    dest.add(srcNumber.longValue());
                    break;
                case BIG_INTEGER:
                    dest.addPOJO(srcNumber.bigIntegerValue()); // use POJO for jackson BigInteger
                    break;
                case FLOAT:
                    dest.add(srcNumber.floatValue()); // use Double for Jackson Float
                    break;
                case DOUBLE:
                    dest.add(srcNumber.doubleValue());
                    break;
                case BIG_DECIMAL:
                    dest.addPOJO(srcNumber.decimalValue()); // use POJO for jackson BigDecimal
                    break;
                }
                break;
            case POJO:
                dest.addPOJO(((FxPOJONode) srcElt).getValue());
                break;
            case STRING:
                dest.add(((FxTextNode) srcElt).textValue());
                break;
            default:
            throw new RuntimeException();
            }
        }
    }
    
    public static void fillFxObjToJsonObj(ObjectNode dest, FxObjNode src) {
        for(Iterator<Entry<String, FxNode>> iter = src.fields(); iter.hasNext(); ) {
            Entry<String, FxNode> e = iter.next();
            String field = e.getKey();
            FxNode srcElt = e.getValue();
            switch (srcElt.getNodeType()) {
                case ARRAY:
                    ArrayNode eltArray = dest.putArray(field);
                    fillFxArrayToJsonArray(eltArray, (FxArrayNode) srcElt);
                    break;
                case OBJECT:
                    ObjectNode eltObj = dest.putObject(field);
                    fillFxObjToJsonObj(eltObj, (FxObjNode) srcElt);
                    break;
                case BINARY:
                    dest.put(field, ((FxBinaryNode) srcElt).binaryValue());
                    break;
                case BOOLEAN:
                    dest.put(field, ((FxBoolNode) srcElt).booleanValue());
                    break;
                case MISSING:
                    //?? skip / use null
                    break;
                case NULL:
                    dest.putNull(field);
                    break;
                case NUMBER:
                    FxValueNode srcNumber = (FxValueNode) srcElt;
                    FxNumberType numberType = srcElt.numberType();
                    switch(numberType) {
                    case INT:
                        dest.put(field, srcNumber.intValue());
                        break;
                    case LONG:
                        dest.put(field, srcNumber.longValue());
                        break;
                    case BIG_INTEGER:
                        dest.putPOJO(field, srcNumber.bigIntegerValue()); // use POJO for jackson BigInteger
                        break;
                    case FLOAT:
                        dest.put(field, srcNumber.floatValue()); // use Double for Jackson Float
                        break;
                    case DOUBLE:
                        dest.put(field, srcNumber.doubleValue());
                        break;
                    case BIG_DECIMAL:
                        dest.putPOJO(field, srcNumber.decimalValue()); // use POJO for jackson BigDecimal
                        break;
                    }
                    break;
                case POJO:
                    dest.putPOJO(field, ((FxPOJONode) srcElt).getValue());
                    break;
               case STRING:
                    dest.put(field, ((FxTextNode) srcElt).textValue());
                    break;
                default:
                throw new RuntimeException();
            }
        }
    }    

    // ------------------------------------------------------------------------

    public static NumberType numberType2Jackson(FxNumberType value) {
        if (value == null) return null;
        switch(value) {
        case INT: return NumberType.INT; 
        case LONG: return NumberType.LONG; 
        case BIG_INTEGER: return NumberType.BIG_INTEGER; 
        case FLOAT: return NumberType.FLOAT; 
        case DOUBLE: return NumberType.DOUBLE; 
        case BIG_DECIMAL: return NumberType.BIG_DECIMAL;
        default: return null;
        }
    }

    /** helper for jsonNodeToFxTree on map */
    public static <K> Map<K,FxNode> jsonNodesToFxTrees(Map<K,JsonNode> src, FxSourceLoc source) {
        Map<K,FxNode> res = new LinkedHashMap<>();
        jsonNodesToFxTrees(res, src, source);
        return res;
    }

    /** helper for jsonNodeToFxTree on map */
    public static <K> void jsonNodesToFxTrees(Map<K,FxNode> res, Map<K,JsonNode> src, FxSourceLoc source) {
        if (src != null && !src.isEmpty()) {
            for(Map.Entry<K,JsonNode> e : src.entrySet()) {
                FxNode resValue = jsonNodeToFxTree(e.getValue(), source);
                res.put(e.getKey(), resValue);
            }
        }
    }

    /** helper for jsonNodeToFxTree on List */
    public static List<FxNode> jsonNodesToFxTrees(Collection<JsonNode> src, FxSourceLoc source) {
        List<FxNode> res = new ArrayList<>();
        jsonNodesToFxTrees(res, src, source);
        return res;
    }

    /** helper for jsonNodeToFxTree on List */
    public static void jsonNodesToFxTrees(Collection<FxNode> res, Collection<JsonNode> src, FxSourceLoc source) {
        if (src != null && !src.isEmpty()) {
            for(JsonNode e : src) {
                FxNode resValue = jsonNodeToFxTree(e, source);
                res.add(resValue);
            }
        }
    }
    
    // ------------------------------------------------------------------------
    
    /** helper for jsonNodeToFxTree on map */
    public static <K> Map<K,JsonNode> fxTreesToJsonNodes(Map<K,FxNode> src) {
        Map<K,JsonNode> res = new LinkedHashMap<>();
        fxTreesToJsonNodes(res, src);
        return res;
    }

    /** helper for jsonNodeToFxTree on map */
    public static <K> void fxTreesToJsonNodes(Map<K,JsonNode> res, Map<K,FxNode> src) {
        if (src != null && !src.isEmpty()) {
            for(Map.Entry<K,FxNode> e : src.entrySet()) {
                JsonNode resValue = fxTreeToJsonNode(e.getValue());
                res.put(e.getKey(), resValue);
            }
        }
    }

    /** helper for jsonNodeToFxTree on List */
    public static List<JsonNode> fxTreesToJsonNodes(Collection<FxNode> src) {
        List<JsonNode> res = new ArrayList<>();
        fxTreesToJsonNodes(res, src);
        return res;
    }

    /** helper for jsonNodeToFxTree on List */
    public static void fxTreesToJsonNodes(Collection<JsonNode> res, Collection<FxNode> src) {
        if (src != null && !src.isEmpty()) {
            for(FxNode e : src) {
                JsonNode resValue = fxTreeToJsonNode(e);
                res.add(resValue);
            }
        }
    }
    
}
