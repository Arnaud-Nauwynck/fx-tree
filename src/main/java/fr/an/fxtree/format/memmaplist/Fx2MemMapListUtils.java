package fr.an.fxtree.format.memmaplist;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.an.fxtree.impl.util.FxUtils;
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

/**
 * List/Map/Objects<->FxTree converter utility
 *
 */
public final class Fx2MemMapListUtils {

    /** private to force all static */
    private Fx2MemMapListUtils() {
    }
    
    // Conversion in-memory Map,List,Values... -> FxNode 
    // ------------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    public static FxNode objectToFxTree(FxChildWriter dest, Object srcObj) {
        FxNode res;
        if (srcObj == null) {
            res = dest.addNull();
        } else if (srcObj instanceof Map) {
            FxObjNode destObj = dest.addObj();
            objectMapToFxTree(destObj, (Map<Object,Object>)srcObj);
            res = destObj;
        } else if (srcObj instanceof Collection) {
            FxArrayNode destArray = dest.addArray();
            objectListToFxArrayTree(destArray, (Collection<Object>)srcObj);
            res = destArray;
            
        } else if (srcObj instanceof String) {
            res = dest.add((String) srcObj);
        } else if (srcObj instanceof Boolean) {
            boolean value = ((Boolean) srcObj).booleanValue();
            res = dest.add(value);
        } else if (srcObj instanceof Integer) {
            int value = ((Integer) srcObj).intValue();
            res = dest.add(value);
        } else if (srcObj instanceof Long) {
            long value = ((Long) srcObj).longValue();
            res = dest.add(value);
        } else if (srcObj instanceof BigInteger) {
            res = dest.add((BigInteger) srcObj);
        } else if (srcObj instanceof Double) {
            double value = ((Double) srcObj).doubleValue();
            res = dest.add(value);
       } else if (srcObj instanceof Float) {
            float value = ((Float) srcObj).floatValue();
            res = dest.add(value);
        } else if (srcObj instanceof BigDecimal) {
            res = dest.add((BigDecimal) srcObj);
        } else if (srcObj instanceof Date) {
            res = dest.addPOJO(srcObj); // add java.util.Date as POJO

        } else {
            // fail-through (unrecognized type?) => use POJO ..
            res = dest.addPOJO(srcObj);
        }
        
        return res;
    }

    private static void objectListToFxArrayTree(FxArrayNode destArray, Collection<?> srcList) {
        FxChildWriter destEltWriter = destArray.insertBuilder();
        for(Object srcElt : srcList) {
            // recurse
            objectToFxTree(destEltWriter, srcElt);
        }
    }

    private static void objectMapToFxTree(FxObjNode dest, Map<Object,Object> src) {
        for(Map.Entry<Object,Object> e : src.entrySet()) {
            Object srcKey = e.getKey();
            Object srcValue = e.getValue();
            
            String keyText;
            if (srcKey instanceof String) {
                keyText = (String) srcKey;
            } else {
                throw FxUtils.notImplYet();
            }
            
            FxChildWriter destValueWriter = dest.putBuilder(keyText);
            // recurse
            objectToFxTree(destValueWriter, srcValue);
        }
    }

    // Conversion FxNode -> in-memory Map,List,Values...  
    // ------------------------------------------------------------------------

    public static Object fxTreeToObject(FxNode src) {
        if (src == null) {
            return null;
        }
        Object res;
        switch (src.getNodeType()) {
            case ARRAY:
                ArrayList<Object> destArray = new ArrayList<>();
                fxArrayToObjList(destArray, (FxArrayNode) src);
                res = destArray;
                break;
            case OBJECT:
                Map<String,Object> destObj = new HashMap<>();
                fxObjToObjMap(destObj, (FxObjNode) src);
                res = destObj;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return res;
    }
        
    public static void fxArrayToObjList(List<Object> dest, FxArrayNode src) {
//        if (! dest.isEmpty()) {
//            dest.removeAll();
//        }
        final int len = src.size();
        for (int i = 0; i < len; i++) {
            FxNode srcElt = src.get(i);
            switch (srcElt.getNodeType()) {
            case ARRAY:
                List<Object> eltArray = new ArrayList<>();
                dest.add(eltArray);
                fxArrayToObjList(eltArray, (FxArrayNode) srcElt);
                break;
            case OBJECT:
                Map<String,Object> eltObj = new LinkedHashMap<>();
                dest.add(eltObj);
                fxObjToObjMap(eltObj, (FxObjNode) srcElt);
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
                dest.add(null);
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
                    dest.add(srcNumber.bigIntegerValue()); // use POJO for jackson BigInteger
                    break;
                case FLOAT:
                    dest.add(srcNumber.floatValue()); // use Double for Jackson Float
                    break;
                case DOUBLE:
                    dest.add(srcNumber.doubleValue());
                    break;
                case BIG_DECIMAL:
                    dest.add(srcNumber.decimalValue()); // use POJO for jackson BigDecimal
                    break;
                }
                break;
            case POJO:
                dest.add(((FxPOJONode) srcElt).getValue());
                break;
            case STRING:
                dest.add(((FxTextNode) srcElt).textValue());
                break;
            default:
            throw new RuntimeException();
            }
        }
    }
    
    public static void fxObjToObjMap(Map<String,Object> dest, FxObjNode src) {
        for(Iterator<Entry<String, FxNode>> iter = src.fields(); iter.hasNext(); ) {
            Entry<String, FxNode> e = iter.next();
            String field = e.getKey();
            FxNode srcElt = e.getValue();
            switch (srcElt.getNodeType()) {
                case ARRAY:
                    ArrayList<Object> eltArray = new ArrayList<>();
                    dest.put(field, eltArray);
                    fxArrayToObjList(eltArray, (FxArrayNode) srcElt);
                    break;
                case OBJECT:
                    Map<String,Object> eltObj = new LinkedHashMap<>();
                    dest.put(field, eltObj);
                    fxObjToObjMap(eltObj, (FxObjNode) srcElt);
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
                    dest.put(field, null);
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
                        dest.put(field, srcNumber.bigIntegerValue()); // use POJO for jackson BigInteger
                        break;
                    case FLOAT:
                        dest.put(field, srcNumber.floatValue()); // use Double for Jackson Float
                        break;
                    case DOUBLE:
                        dest.put(field, srcNumber.doubleValue());
                        break;
                    case BIG_DECIMAL:
                        dest.put(field, srcNumber.decimalValue());
                        break;
                    }
                    break;
                case POJO:
                    dest.put(field, ((FxPOJONode) srcElt).getValue());
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