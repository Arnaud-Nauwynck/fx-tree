package fr.an.fxtree.json.jackson;

import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TextNode;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxNodeFactoryRegistry;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxRootDocument;

public class Jackson2FxTreeBuilder {

    public static void buildTree(FxRootDocument dest, JsonNode src) {
        FxNodeFactoryRegistry factory = dest.getNodeFactory();
        switch (src.getNodeType()) {
        case ARRAY:
            FxArrayNode destArray = factory.newArray();
            dest.setContent(destArray);
            buildTree(destArray, (ArrayNode) src);
            break;
        case OBJECT:
            FxObjNode destObj = factory.newObj();
            dest.setContent(destObj);
            buildTree(destObj, (ObjectNode) src);
            break;
        default:
            dest.setContent(buildValue(factory, src));
            break;
        }
    }

    public static FxNode buildValue(FxNodeFactoryRegistry factory, JsonNode src) {
        switch (src.getNodeType()) {
        case ARRAY:
            throw new IllegalStateException("not a value");
        case BINARY:
            return factory.newBinary(((BinaryNode) src).binaryValue());
        case BOOLEAN:
            return factory.newBool(((BooleanNode) src).booleanValue());
        case MISSING:
            //?? use null
            return null;
        case NULL:
            return factory.newNull();
        case NUMBER:
            NumericNode srcNumber = (NumericNode) src;
            JsonParser.NumberType numberType = src.numberType();
            switch(numberType) {
            case INT:
                return factory.newInt(srcNumber.intValue());
            case LONG:
                return factory.newLong(srcNumber.longValue());
            case BIG_INTEGER:
                return factory.newPOJO(srcNumber.bigIntegerValue()); // use POJO for jackson BigInteger
            case FLOAT:
                return factory.newDouble(srcNumber.floatValue()); // use Double for Jackson Float
            case DOUBLE:
                return factory.newDouble(srcNumber.doubleValue());
            case BIG_DECIMAL:
                return factory.newPOJO(srcNumber.decimalValue()); // use POJO for jackson BigDecimal
            default:
                throw new RuntimeException();
            }
        case OBJECT:
            throw new IllegalStateException("not a value");
        case POJO:
            return factory.newPOJO(((POJONode) src).getPojo());
        case STRING:
            return factory.newText(((TextNode) src).textValue());
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

    
}
