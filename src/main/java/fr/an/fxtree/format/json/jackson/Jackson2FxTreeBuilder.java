package fr.an.fxtree.format.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Iterator;
import java.util.Map.Entry;

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

public class Jackson2FxTreeBuilder {

    public static FxNode buildTree(FxChildWriter dest, JsonNode src) {
        FxNode res;
        switch (src.getNodeType()) {
        case ARRAY:
            FxArrayNode destArray = dest.addArray();
            buildTree(destArray, (ArrayNode) src);
            res = destArray;
            break;
        case OBJECT:
            FxObjNode destObj = dest.addObj();
            buildTree(destObj, (ObjectNode) src);
            res = destObj;
            break;
        default:
            res = buildValue(dest, src);
            break;
        }
        return res;
    }

    public static FxNode buildValue(FxChildWriter dest, JsonNode src) {
        switch (src.getNodeType()) {
        case ARRAY:
            throw new IllegalStateException("not a value");
        case BINARY:
            return dest.add(((BinaryNode) src).binaryValue());
        case BOOLEAN:
            return dest.add(((BooleanNode) src).booleanValue());
        case MISSING:
            //?? use null
            return null;
        case NULL:
            return dest.addNull();
        case NUMBER:
            NumericNode srcNumber = (NumericNode) src;
            JsonParser.NumberType numberType = src.numberType();
            switch(numberType) {
            case INT:
               return dest.add(srcNumber.intValue());
            case LONG:
                return dest.add(srcNumber.longValue());
            case BIG_INTEGER:
                return dest.addPOJO(srcNumber.bigIntegerValue()); // use POJO for jackson BigInteger
            case FLOAT:
                return dest.add(srcNumber.floatValue()); // use Double for Jackson Float
            case DOUBLE:
                return dest.add(srcNumber.doubleValue());
            case BIG_DECIMAL:
                return dest.addPOJO(srcNumber.decimalValue()); // use POJO for jackson BigDecimal
            default:
                throw new RuntimeException();
            }
        case OBJECT:
            throw new IllegalStateException("not a value");
        case POJO:
            return dest.addPOJO(((POJONode) src).getPojo());
        case STRING:
            return dest.add(((TextNode) src).textValue());
        default:
            throw new RuntimeException();
        }
    }
    
    public static void buildTree(FxArrayNode dest, ArrayNode src) {
        if (! dest.isEmpty()) {
            dest.removeAll();
        }
        final int len = src.size();
        for (int i = 0; i < len; i++) {
            JsonNode srcElt = src.get(i);
            switch (srcElt.getNodeType()) {
            case ARRAY:
                FxArrayNode eltArray = dest.addArray();
                buildTree(eltArray, (ArrayNode) srcElt);
                break;
            case OBJECT:
                FxObjNode eltObj = dest.addObj();
                buildTree(eltObj, (ObjectNode) srcElt);
                break;
            case BINARY:
                dest.add(((BinaryNode) srcElt).binaryValue());
                break;
            case BOOLEAN:
                dest.add(((BooleanNode) srcElt).booleanValue());
                break;
            case MISSING:
                //?? skip / use null
                break;
            case NULL:
                dest.addNull();
                break;
            case NUMBER:
                NumericNode srcNumber = (NumericNode) srcElt;
                JsonParser.NumberType numberType = srcElt.numberType();
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
                dest.addPOJO(((POJONode) srcElt).getPojo());
                break;
            case STRING:
                dest.add(((TextNode) srcElt).textValue());
                break;
            default:
            throw new RuntimeException();
            }
        }
    }
    
    
    public static void buildTree(FxObjNode dest, ObjectNode src) {
        if (! dest.isEmpty()) {
            dest.removeAll();
        }
        for (Iterator<Entry<String, JsonNode>> iter = src.fields(); iter.hasNext(); ) {
            Entry<String, JsonNode> e = iter.next();
            String field = e.getKey();
            JsonNode srcElt = e.getValue();
            switch (srcElt.getNodeType()) {
            case ARRAY:
                FxArrayNode eltArray = dest.putArray(field);
                buildTree(eltArray, (ArrayNode) srcElt);
                break;
            case OBJECT:
                FxObjNode eltObj = dest.putObj(field);
                buildTree(eltObj, (ObjectNode) srcElt);
                break;
            case BINARY:
                dest.put(field, ((BinaryNode) srcElt).binaryValue());
                break;
            case BOOLEAN:
                dest.put(field, ((BooleanNode) srcElt).booleanValue());
                break;
            case MISSING:
                //?? skip / use null
                break;
            case NULL:
                dest.putNull(field);
                break;
            case NUMBER:
                NumericNode srcNumber = (NumericNode) srcElt;
                JsonParser.NumberType numberType = srcElt.numberType();
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
                dest.putPOJO(field, ((POJONode) srcElt).getPojo());
                break;
            case STRING:
                dest.put(field, ((TextNode) srcElt).textValue());
                break;
            default:
            throw new RuntimeException();
            }
        }
    }

    // conversion FxNode -> Jackson JsonNode
    // ------------------------------------------------------------------------

    public static JsonNode buildJacksonTree(FxNode src) {
        return buildJacksonTree(src, JsonNodeFactory.instance);
    }
    
    public static JsonNode buildJacksonTree(FxNode src, JsonNodeFactory jsonNodeFactory) {
        if (src == null) {
            return null;
        }
        JsonNode res;
        switch (src.getNodeType()) {
            case ARRAY:
                ArrayNode destArray = new ArrayNode(jsonNodeFactory);
                buildJacksonTree(destArray, (FxArrayNode) src);
                res = destArray;
                break;
            case OBJECT:
                ObjectNode destObj = new ObjectNode(jsonNodeFactory);
                buildJacksonTree(destObj, (FxObjNode) src);
                res = destObj;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return res;
    }
        
    public static void buildJacksonTree(ArrayNode dest, FxArrayNode src) {
//        if (! dest.isEmpty()) {
//            dest.removeAl();
//        }
        final int len = src.size();
        for (int i = 0; i < len; i++) {
            FxNode srcElt = src.get(i);
            switch (srcElt.getNodeType()) {
            case ARRAY:
                ArrayNode eltArray = dest.addArray();
                buildJacksonTree(eltArray, (FxArrayNode) srcElt);
                break;
            case OBJECT:
                ObjectNode eltObj = dest.addObject();
                buildJacksonTree(eltObj, (FxObjNode) srcElt);
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
    
    public static void buildJacksonTree(ObjectNode dest, FxObjNode src) {
        for(Iterator<Entry<String, FxNode>> iter = src.fields(); iter.hasNext(); ) {
            Entry<String, FxNode> e = iter.next();
            String field = e.getKey();
            FxNode srcElt = e.getValue();
            switch (srcElt.getNodeType()) {
                case ARRAY:
                    ArrayNode eltArray = dest.putArray(field);
                    buildJacksonTree(eltArray, (FxArrayNode) srcElt);
                    break;
                case OBJECT:
                    ObjectNode eltObj = dest.putObject(field);
                    buildJacksonTree(eltObj, (FxObjNode) srcElt);
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
}