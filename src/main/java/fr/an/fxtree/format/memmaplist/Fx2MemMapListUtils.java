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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxBoolNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxDoubleNode;
import fr.an.fxtree.model.FxIntNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxNumberType;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.FxTextNode;
import fr.an.fxtree.model.FxValueNode;
import fr.an.fxtree.model.path.FxNodePath;

/**
 * List/Map/Objects<->FxTree converter utility
 *
 */
public final class Fx2MemMapListUtils {

    private static final Logger LOG = LoggerFactory.getLogger(Fx2MemMapListUtils.class);

    /** private to force all static */
    private Fx2MemMapListUtils() {
    }

    private static final FxSourceLoc LOC_inMem = FxSourceLoc.inMem();
    
    public static FxChildWriter inMemWriter() {
        return new FxMemRootDocument(LOC_inMem).contentWriter();
    }
    
    // Conversion in-memory Map,List,Values... -> FxNode 
    // ------------------------------------------------------------------------

    public static FxObjNode newObjNode() {
        return inMemWriter().addObj(LOC_inMem);
    }
    
    public static FxBoolNode valueToTree(boolean value) {
        return inMemWriter().add(value, LOC_inMem);
    }

    public static FxIntNode valueToTree(int value) {
        return inMemWriter().add(value, LOC_inMem);
    }

    public static FxDoubleNode valueToTree(double value) {
        return inMemWriter().add(value, LOC_inMem);
    }

    public static FxTextNode valueToTree(String value) {
        return inMemWriter().add(value, LOC_inMem);
    }

    
    @SuppressWarnings("unchecked")
    public static FxNode valueToTree(FxChildWriter dest, Object srcObj, FxSourceLoc loc) {
        FxNode res;
        if (srcObj == null) {
            res = dest.addNull(loc);
        } else if (srcObj instanceof Map) {
            FxObjNode destObj = dest.addObj(loc);
            fillKeyValuesToTree(destObj, (Map<Object,Object>)srcObj, loc);
            res = destObj;
        } else if (srcObj instanceof Collection) {
            FxArrayNode destArray = dest.addArray(loc);
            fillValuesToTree(destArray, (Collection<Object>)srcObj, loc);
            res = destArray;
            
        } else if (srcObj instanceof String) {
            res = dest.add((String) srcObj, loc);
        } else if (srcObj instanceof Boolean) {
            boolean value = ((Boolean) srcObj).booleanValue();
            res = dest.add(value, loc);
        } else if (srcObj instanceof Integer) {
            int value = ((Integer) srcObj).intValue();
            res = dest.add(value, loc);
        } else if (srcObj instanceof Long) {
            long value = ((Long) srcObj).longValue();
            res = dest.add(value, loc);
        } else if (srcObj instanceof BigInteger) {
            res = dest.add((BigInteger) srcObj, loc);
        } else if (srcObj instanceof Double) {
            double value = ((Double) srcObj).doubleValue();
            res = dest.add(value, loc);
       } else if (srcObj instanceof Float) {
            float value = ((Float) srcObj).floatValue();
            res = dest.add(value, loc);
        } else if (srcObj instanceof BigDecimal) {
            res = dest.add((BigDecimal) srcObj, loc);
        } else if (srcObj instanceof Date) {
            res = dest.addPOJO(srcObj, loc); // add java.util.Date as POJO

        } else {
            // fail-through (unrecognized type?) => use POJO ..
            res = dest.addPOJO(srcObj, loc);
        }
        
        return res;
    }

    private static void fillValuesToTree(FxArrayNode destArray, Collection<?> srcList, FxSourceLoc loc) {
        FxChildWriter destEltWriter = destArray.insertBuilder();
        for(Object srcElt : srcList) {
            // recurse
            valueToTree(destEltWriter, srcElt, loc);
        }
    }

    private static void fillKeyValuesToTree(FxObjNode dest, Map<Object,Object> src, FxSourceLoc loc) {
        for(Map.Entry<Object,Object> e : src.entrySet()) {
            Object srcKey = e.getKey();
            Object srcValue = e.getValue();
            
            String keyText;
            if (srcKey instanceof String) {
                keyText = (String) srcKey;
            } else {
                FxNodePath currDestPath = FxNodeValueUtils.nodeToAncestorPath(dest);
                String currPath = currDestPath.toString();
                String err = "unrecognised object key, expecting String 'key: value', got key:'" + srcKey + "'"
                        + " at '" + currPath + "'";
                if (srcKey instanceof Map) {
                    err += " ... maybe you put 'key: {{value}}' but '{ }' is a special object un yaml, to be escaped with ' '";
                }
                // LOG.error("Failed ot parse yaml, SKIP key, no throw!! " + err);
                // continue;
                throw new IllegalArgumentException(err);
            }
            if (srcValue == null) {
                FxNodePath currDestPath = FxNodeValueUtils.nodeToAncestorPath(dest);
                String currPath = currDestPath.toString();
                LOG.warn("Detected incorrect yaml, null value for key: '" + keyText + "'"
                        + " replaced with empty string ''"
                        + " at '" + currPath + "'");                
                FxChildWriter destValueWriter = dest.putBuilder(keyText);
                destValueWriter.add("", loc);
                continue;
            }
            
            FxChildWriter destValueWriter = dest.putBuilder(keyText);
            // recurse
            valueToTree(destValueWriter, srcValue, loc);
        }
    }

    public static Map<String,FxNode> namedValuesToTrees(Map<String,Object> namedValues,
            FxSourceLoc loc) {
        Map<String,FxNode> res = new HashMap<>();
        namedValuesToTrees(res, namedValues, loc);
        return res;
    }
    
    public static void namedValuesToTrees(Map<String,FxNode> dest, 
            Map<String,Object> namedValues, FxSourceLoc loc) {
        FxMemRootDocument doc = FxMemRootDocument.newInMem();
        FxObjNode objNode = doc.contentWriter().addObj(loc);
        for(Map.Entry<String,Object> e : namedValues.entrySet()) {
            String name = e.getKey();
            Object value = e.getValue();
            FxNode valueNode = valueToTree(objNode.putBuilder(name), value, loc);
            dest.put(name, valueNode);
        }
    }
    
    public static void namedValueToTree(Map<String,FxNode> dest, 
            String name, Object value, FxSourceLoc loc) {
        FxMemRootDocument doc = new FxMemRootDocument(loc); // TOCHECK
        FxObjNode objNode = doc.contentWriter().addObj(loc);
        FxNode valueNode = valueToTree(objNode.putBuilder(name), value, loc);
        dest.put(name, valueNode);
    }
    
    // Conversion FxNode -> in-memory Map,List,Values...  
    // ------------------------------------------------------------------------

    public static Object treeToValue(FxNode src) {
        if (src == null) {
            return null;
        }
        Object res;
        switch (src.getNodeType()) {
            case ARRAY:
                ArrayList<Object> destArray = new ArrayList<>();
                fillArrayTreeToValues(destArray, (FxArrayNode) src);
                res = destArray;
                break;
            case OBJECT:
                Map<String,Object> destObj = new HashMap<>();
                fillObjTreeToKeyValues(destObj, (FxObjNode) src);
                res = destObj;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return res;
    }

    public static List<Object> arrayTreeToValues(FxArrayNode src) { 
        List<Object> res = new ArrayList<>();
        fillArrayTreeToValues(res, src);
        return res;
    }

    
    public static void fillArrayTreeToValues(Collection<Object> dest, FxArrayNode src) {
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
                fillArrayTreeToValues(eltArray, (FxArrayNode) srcElt);
                break;
            case OBJECT:
                Map<String,Object> eltObj = new LinkedHashMap<>();
                dest.add(eltObj);
                fillObjTreeToKeyValues(eltObj, (FxObjNode) srcElt);
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
    
    public static Map<String,Object> objTreeToValues(FxObjNode src) { 
        LinkedHashMap<String,Object> res = new LinkedHashMap<>();
        fillObjTreeToKeyValues(res, src);
        return res;
    }
    
    public static Map<String,String> objTreeToMapStringString(FxObjNode src) { 
        LinkedHashMap<String,String> res = new LinkedHashMap<>();
        for(Iterator<Entry<String, FxNode>> iter = src.fields(); iter.hasNext(); ) {
            Entry<String, FxNode> e = iter.next();
            String field = e.getKey();
            FxNode srcElt = e.getValue();
            switch (srcElt.getNodeType()) {
            case STRING:
                res.put(field, ((FxTextNode) srcElt).textValue());
                break;
            default:
                throw new RuntimeException();
            }
        }
        return res;
    }
    
    public static void fillObjTreeToKeyValues(Map<String,Object> dest, FxObjNode src) {
        for(Iterator<Entry<String, FxNode>> iter = src.fields(); iter.hasNext(); ) {
            Entry<String, FxNode> e = iter.next();
            String field = e.getKey();
            FxNode srcElt = e.getValue();
            switch (srcElt.getNodeType()) {
                case ARRAY:
                    ArrayList<Object> eltArray = new ArrayList<>();
                    dest.put(field, eltArray);
                    fillArrayTreeToValues(eltArray, (FxArrayNode) srcElt);
                    break;
                case OBJECT:
                    Map<String,Object> eltObj = new LinkedHashMap<>();
                    dest.put(field, eltObj);
                    fillObjTreeToKeyValues(eltObj, (FxObjNode) srcElt);
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