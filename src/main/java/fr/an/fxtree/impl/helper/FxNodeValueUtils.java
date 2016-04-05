package fr.an.fxtree.impl.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.an.fxtree.impl.util.FxUtils;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.path.FxChildPathElement;
import fr.an.fxtree.model.path.FxNodeOuterPath;
import fr.an.fxtree.model.path.FxNodePath;

/**
 * utility static methods for extracting / converting / type-checking nodes
 * 
 * see also FxObjValueHelper 
 */
public final class FxNodeValueUtils {

    private static final String STRING_ARRAY_FORMAT = "CSV 'str1,str2...' or array ['str1, 'str2'..]";
    private static final String STRING_FLATTEN_ARRAY_FORMAT = "CSV 'str1,str2...' or array ['str1, 'str2'..] or flattenize [ 'str1', [ 'str2', 'str3' ]]";
    
    private FxNodeValueUtils() {
    }

    public static FxNode getOrThrow(FxObjNode parent, String fieldName) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            throw new IllegalArgumentException("expecting argument '" + fieldName + "'");
        }
        return fieldNode;
    }
    
    public static String getOrDefault(FxObjNode parent, String fieldName, String defaultValue) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return defaultValue;
        }
        return nodeToString(fieldNode);
    }

    public static String getString(FxObjNode parent, String fieldName) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return null;
        }
        return nodeToString(fieldNode);
    }

    public static String getStringOrThrow(FxObjNode parent, String fieldName) {
        FxNode fieldNode = getOrThrow(parent, fieldName);
        return nodeToString(fieldNode);
    }

    public static String nodeToString(FxNode fieldNode) {
        if (!fieldNode.isTextual()) {
            throw new IllegalArgumentException("expecting String, got " + fieldNode.getNodeType());
        }
        return fieldNode.textValue();
    }
    
    public static int getOrDefault(FxObjNode parent, String fieldName, int defaultValue) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return defaultValue;
        }
        return nodeToInt(fieldNode);
    }
    
    public static FxObjNode getObjOrThrow(FxObjNode parent, String fieldName) {
        FxNode fieldNode = getOrThrow(parent, fieldName);
        return nodeToObj(fieldNode);
    }

    public static FxObjNode getObjOrNull(FxObjNode parent, String fieldName) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return null;
        }
        return nodeToObj(fieldNode);
    }
    
    public static FxObjNode nodeToObj(FxNode fieldNode) {
        if (!fieldNode.isObject()) {
            throw new IllegalArgumentException("expecting Object, got " + fieldNode.getNodeType());
        }
        return (FxObjNode) fieldNode;
    }
    
    /* Boolean : getBooleanOrThrow, getOrDefault, nodeToBoolean */
    
    public static boolean getBooleanOrThrow(FxObjNode parent, String fieldName) {
        FxNode fieldNode = getOrThrow(parent, fieldName);
        return nodeToBoolean(fieldNode);
    }
    
    public static boolean getBooleanOrDefault(FxObjNode parent, String fieldName, boolean defaultValue) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return defaultValue;
        }
        return nodeToBoolean(fieldNode);
    }
    
    public static boolean nodeToBoolean(FxNode src) {
        boolean res;
        if (src.isBoolean()) {
            res = src.booleanValue();
        } else if (src.isTextual()) {
            // also accept "true"/"false", "y"/"n", "yes"/"no" ...
            String text = src.textValue();
            switch(text) {
            case "true": case "True": case "TRUE": case "y": case "Y": case "yes": case "Yes": case "YES": 
                res = true; 
                break;
            case "false": case "False": case "FALSE": case "n": case "N": case "no": case "No": case "NO":   
                res = false; 
                break;
            default:
                throw new IllegalArgumentException("expecting boolean argument '" + src + "', or true/false, y/n, yes/no .. got text '" + text + "'");
            }
        } else if (src.isNumber()) {
            int val = src.intValue();
            res = (val != 0);
        } else {
            throw new IllegalArgumentException("expecting boolean argument '" + src + "'");
        }
        return res;
    }


    /* Char: getCharOrDefault, getCharOrThrow, nodeToChar */
    
    public static char getCharOrDefault(FxObjNode parent, String fieldName, char defaultValue) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return defaultValue;
        }
        return nodeToChar(fieldNode);
    }
    
    public static char getCharOrThrow(FxObjNode parent, String fieldName) {
        FxNode fieldNode = getOrThrow(parent, fieldName);
        if (!fieldNode.isNumber()) {
            throw new IllegalArgumentException("expecting Char argument '" + fieldName + "', got " + fieldNode.getNodeType());
        }
        return nodeToChar(fieldNode);
    }

    public static char nodeToChar(FxNode fieldNode) {
        char res;
        if (fieldNode.isTextual()) {
            String text = fieldNode.textValue();
            if (text.length() == 0 || text.length() > 1) {
                throw new IllegalArgumentException("expecting 'char', got string length != 1: '" + text + "'");
            }
            res = text.charAt(0);
        } else if (fieldNode.isInt()) {
            res = (char) fieldNode.intValue();
        } else {
            throw new IllegalArgumentException("expecting 'char', got " + fieldNode.getNodeType());
        }
        return res;
    }

    /* Int: getIntOrDefault, getIntOrThrow, nodeToInt */
    
    public static int getIntOrDefault(FxObjNode parent, String fieldName, int defaultValue) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return defaultValue;
        }
        return nodeToInt(fieldNode);
    }
    
    public static int getInt(FxObjNode parent, String fieldName) {
        return getIntOrThrow(parent, fieldName);
    }
    
    public static int getIntOrThrow(FxObjNode parent, String fieldName) {
        FxNode fieldNode = getOrThrow(parent, fieldName);
        if (!fieldNode.isNumber()) {
            throw new IllegalArgumentException("expecting int argument '" + fieldName + "', got " + fieldNode.getNodeType());
        }
        return nodeToInt(fieldNode);
    }

    public static int nodeToInt(FxNode fieldNode) {
        if (!fieldNode.isNumber()) {
            throw new IllegalArgumentException("expecting 'int', got " + fieldNode.getNodeType());
        }
        int res = fieldNode.intValue();
        return res;
    }

    /* Long: getLongOrDefault, getLongOrThrow, nodeToLong */
    
    public static long getLongOrDefault(FxObjNode parent, String fieldName, Long defaultValue) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return defaultValue;
        }
        return nodeToLong(fieldNode);
    }
    
    public static long getLongOrThrow(FxObjNode parent, String fieldName) {
        FxNode fieldNode = getOrThrow(parent, fieldName);
        if (!fieldNode.isNumber()) {
            throw new IllegalArgumentException("expecting Long argument '" + fieldName + "', got " + fieldNode.getNodeType());
        }
        return nodeToLong(fieldNode);
    }

    public static long nodeToLong(FxNode fieldNode) {
        if (!fieldNode.isNumber()) {
            throw new IllegalArgumentException("expecting 'Long', got " + fieldNode.getNodeType());
        }
        return fieldNode.longValue();
    }
    
    /* Double: getDoubleOrDefault, getDoubleOrThrow, nodeToDouble */
    
    public static double getDoubleOrDefault(FxObjNode parent, String fieldName, double defaultValue) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return defaultValue;
        }
        return nodeToDouble(fieldNode);
    }
    
    public static double getDoubleOrThrow(FxObjNode parent, String fieldName) {
        FxNode fieldNode = getOrThrow(parent, fieldName);
        if (!fieldNode.isNumber()) {
            throw new IllegalArgumentException("expecting 'double' argument '" + fieldName + "', got " + fieldNode.getNodeType());
        }
        return nodeToDouble(fieldNode);
    }

    public static double nodeToDouble(FxNode fieldNode) {
        if (!fieldNode.isNumber()) {
            throw new IllegalArgumentException("expecting 'double', got " + fieldNode.getNodeType());
        }
        return fieldNode.doubleValue();
    }

    /* Float: getFloatOrDefault, getFloatOrThrow, nodeToFloat */

    public static float getFloatOrDefault(FxObjNode parent, String fieldName, float defaultValue) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return defaultValue;
        }
        return nodeToFloat(fieldNode);
    }
    
    public static float getFloatOrThrow(FxObjNode parent, String fieldName) {
        FxNode fieldNode = getOrThrow(parent, fieldName);
        if (!fieldNode.isNumber()) {
            throw new IllegalArgumentException("expecting 'Float' argument '" + fieldName + "', got " + fieldNode.getNodeType());
        }
        return nodeToFloat(fieldNode);
    }

    public static float nodeToFloat(FxNode fieldNode) {
        if (!fieldNode.isNumber()) {
            throw new IllegalArgumentException("expecting 'float', got " + fieldNode.getNodeType());
        }
        return fieldNode.floatValue();
    }

    /* Array */
    
    public static FxArrayNode getArrayOrThrow(FxObjNode parent, String fieldName) {
        FxNode fieldNode = getOrThrow(parent, fieldName);
        return nodeToArray(fieldNode);
    }
    
    public static FxArrayNode getArrayOrNull(FxObjNode parent, String fieldName) {
        FxNode fieldNode = parent.get(fieldName);
        return nodeToArray(fieldNode);
    }

    public static FxArrayNode nodeToArray(FxNode fieldNode) {
        if (fieldNode == null) {
            return null;
        }
        if (!fieldNode.isArray()) {
            throw new IllegalArgumentException("expecting 'array', got " + fieldNode.getNodeType());
        }
        return (FxArrayNode) fieldNode;
    }

    public static String[] getStringArrayOrThrow(FxObjNode parent, String fieldName, boolean allowRecurseFlatten) {
        FxNode fieldNode = getOrThrow(parent, fieldName);
        return nodeToStringArray(fieldNode, allowRecurseFlatten);
    }

    public static String[] getStringArrayOrNull(FxObjNode parent, String fieldName, boolean allowRecurseFlatten) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return null;
        }
        return nodeToStringArray(fieldNode, allowRecurseFlatten);
    }

    public static List<String> nodeToStringList(FxNode value, boolean allowRecurseFlatten) {
        if (value == null) {
            return null;
        }
        List<String> res;
        if (value.isTextual()) {
            String[] tmpres = value.textValue().split(",");
            res = new ArrayList<>(Arrays.asList(tmpres));
        } else if (value.isArray()) {
            FxArrayNode array = (FxArrayNode) value;
            int len = array.size();
            res = new ArrayList<String>(len);
            for(int i = 0; i < len; i++) {
                FxNode child = array.get(i);
                if (child.isTextual()) {
                    res.add(child.textValue());
                } else if (value.isArray()) {
                    if (allowRecurseFlatten) {
                        // recurse
                        List<String> tmpresElts = nodeToStringList(child, allowRecurseFlatten);
                        if (tmpresElts != null) {
                            res.addAll(tmpresElts);
                        }
                    } else {
                        throw new IllegalArgumentException("expected " + STRING_ARRAY_FORMAT);
                    }
                } else {
                    throw new IllegalArgumentException("expected " + STRING_FLATTEN_ARRAY_FORMAT);
                }
            }
        } else {
            throw new IllegalArgumentException("expected " + (allowRecurseFlatten? STRING_FLATTEN_ARRAY_FORMAT : STRING_ARRAY_FORMAT));
        }
        return res;
    }
    
    public static String[] nodeToStringArray(FxNode value, boolean allowRecurseFlatten) {
        List<String> tmpres = nodeToStringList(value, allowRecurseFlatten);
        return tmpres != null? tmpres.toArray(new String[tmpres.size()]) : null;
    }
    

    public static byte[] nodeToByteArray(FxNode src) {
        if (src instanceof FxBinaryNode) {
            byte[] tmpres = ((FxBinaryNode) src).getValue();
            if (tmpres != null) {
                tmpres = tmpres.clone();
            }
            return tmpres;  
        } else {
            throw new IllegalArgumentException("expected byte[], got " + src.getNodeType());
        }
    }
    
    // ------------------------------------------------------------------------

    public static Object nodeToValueForType(FxNode src, Class<?> destType, FxEvalContext evalCtx) {
        if (FxEvalContext.class.isAssignableFrom(destType)) {
            return evalCtx;
        } else {
            return nodeToValueForType(src, destType);
        }
    }
    
    public static Object nodeToValueForType(FxNode src, Class<?> destType) {
        if (destType.isPrimitive()) {
            switch(destType.getName()) {
            case "boolean": return nodeToBoolean(src);
            case "char": return nodeToChar(src);
            case "byte": return (byte) nodeToInt(src); // cf int
            case "short": return (byte) nodeToInt(src); // cf int
            case "int": return nodeToInt(src);
            case "long": return nodeToLong(src);
            case "float": return nodeToFloat(src);
            case "double": return nodeToDouble(src);
            case "void": return null;
            default: throw FxUtils.switchDefault();
            }
        } else if (destType.equals(String.class)) {
            return src.textValue();
        } else if (destType.equals(byte[].class)) {
            return nodeToByteArray(src);
        } else if ((src instanceof FxPOJONode) && destType.isInstance(((FxPOJONode)src).getValue())) {
            return ((FxPOJONode)src).getValue();
        } else if (FxNode.class.isAssignableFrom(destType)) {
            if (destType.isInstance(src)) {
                return src;
            } else {
                throw new IllegalArgumentException("expected " + destType + ", got " + src.getNodeType());
            }
        } else {
            // TODO... use Jackson serialisation?
            throw FxUtils.notImplYet();
        }
    }

    public static FxNodePath nodeToPath(FxNode src) {
        if (src.isArray()) {
            FxArrayNode array = (FxArrayNode) src;
            final int len = array.size();
            FxChildPathElement[] elts = new FxChildPathElement[len]; 
            for(int i = 0; i < len; i++) {
                FxNode e = array.get(i);
                if (e.isNumber()) {
                    int arrayIndex = e.intValue();
                    elts[i] = FxChildPathElement.of(arrayIndex);
                } else if (e.isTextual()) {
                    elts[i] = FxChildPathElement.of(e.textValue());
                } else {
                    throw new IllegalArgumentException("expected jsonpath array containing 'int'(index) or 'string' fieldname, got " + e.getNodeType());    
                }
            }
            return FxNodePath.of(elts);
        } else if (src.isTextual()) {
            String pathText = src.textValue();
            return FxNodePath.parse(pathText);
        } else {
            throw new IllegalArgumentException("expected tree path as string or array, got " + src.getNodeType());
        }
    }

    public static FxNodeOuterPath nodeToOuterPath(FxNode src) {
        if (src.isArray()) {
            FxArrayNode array = (FxArrayNode) src;
            final int len = array.size();
            int parentCount = 0;
            if (len > 0) {
                FxNode parentCountNode = array.get(0);
                parentCount = nodeToInt(parentCountNode);
            }
            FxChildPathElement[] elts = new FxChildPathElement[len-1];            
            for(int i = 1; i < len; i++) {
                FxNode e = array.get(i);
                if (e.isNumber()) {
                    int arrayIndex = e.intValue();
                    elts[i] = FxChildPathElement.of(arrayIndex);
                } else if (e.isTextual()) {
                    elts[i] = FxChildPathElement.of(e.textValue());
                } else {
                    throw new IllegalArgumentException("expected jsonpath array containing 'int'(index) or 'string' fieldname, got " + e.getNodeType());    
                }
            }
            FxNodePath remainPath = FxNodePath.of(elts);
            return FxNodeOuterPath.of(parentCount, remainPath);
        } else if (src.isTextual()) {
            String pathText = src.textValue();
            return FxNodeOuterPath.parse(pathText);
        } else {
            throw new IllegalArgumentException("expected tree outer path as string or array, got " + src.getNodeType());
        }
    }

    
    // TODO .. should be more customizable... currently use equality based on 'id' field for object...  
    public static Object tryExtractId(FxNode src) {
        if (src.isObject()) {
            FxObjNode obj = (FxObjNode) src;
            FxNode idValue = obj.get("id");
            if (idValue != null) {
                if (idValue.isTextual()) {
                    return idValue.textValue(); 
                } else if (idValue.isNumber()) {
                    return idValue.intValue();
                }
            }
        }
        return null;
    }

    public static Map<Object, FxNode> tryIndexEltsByIds(FxArrayNode array) {
        Map<Object, FxNode> res = new HashMap<>();
        int len = array.size();
        for (int i = 0; i < len; i++) {
            FxNode elt = array.get(i);
            Object eltId = FxNodeValueUtils.tryExtractId(elt);
            if (eltId != null) {
                res.put(eltId, elt);
            }
        }
        return res;
    }

}
