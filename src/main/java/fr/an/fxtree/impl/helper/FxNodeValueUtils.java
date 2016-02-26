package fr.an.fxtree.impl.helper;

import java.util.ArrayList;
import java.util.List;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public class FxNodeValueUtils {

    private static final String STRING_ARRAY_FORMAT = "CSV 'str1,str2...' or array ['str1, 'str2'..]";
    private static final String STRING_FLATTEN_ARRAY_FORMAT = "CSV 'str1,str2...' or array ['str1, 'str2'..] or flattenize [ 'str1', [ 'str2', 'str3' ]]";
    
    public static String[] extractStringArray(FxNode value, boolean allowRecurseFlatten) {
        if (value == null) return null;
        String[] res;
        if (value.isTextual()) {
            res = value.textValue().split(",");
        } else if (value.isArray()) {
            FxArrayNode array = (FxArrayNode) value;
            int len = array.size();
            List<String> tmpRes = new ArrayList<String>(len);
            for(int i = 0; i < len; i++) {
                FxNode child = array.get(i);
                if (child.isTextual()) {
                    tmpRes.add(child.textValue());
                } else if (value.isArray()) {
                    if (allowRecurseFlatten) {
                        // recurse
                        String[] tmpresElts = extractStringArray(child, allowRecurseFlatten);
                        for(String e : tmpresElts) {
                            tmpRes.add(e);
                        }
                    } else {
                        throw new IllegalArgumentException("expected " + STRING_ARRAY_FORMAT);
                    }
                } else {
                    throw new IllegalArgumentException("expected " + STRING_FLATTEN_ARRAY_FORMAT);
                }
            }
            res = tmpRes.toArray(new String[tmpRes.size()]);
        } else {
            throw new IllegalArgumentException("expected " + (allowRecurseFlatten? STRING_FLATTEN_ARRAY_FORMAT : STRING_ARRAY_FORMAT));
        }
        return res;
    }
    
    public static String getOrDefault(FxObjNode parent, String fieldName, String defaultValue) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return defaultValue;
        }
        String res = fieldNode.textValue();
        if (res == null) {
            return defaultValue;
        }
        return res;
    }

    public static int getOrDefault(FxObjNode parent, String fieldName, int defaultValue) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return defaultValue;
        }
        int res;
        if (fieldNode.isNumber()) { 
            res = fieldNode.intValue();
        } else {
            // throw error?
            throw new IllegalArgumentException("expecting int argument '" + fieldName + "', got " + fieldNode.getNodeType());
        }
        return res;
    }

    public static int getIntOrThrow(FxObjNode parent, String fieldName) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null || !fieldNode.isNumber()) {
            throw new IllegalArgumentException("expecting int argument '" + fieldName + "', got " + ((fieldNode != null)? fieldNode.getNodeType() : "null"));
        }
        int res = fieldNode.intValue();
        return res;
    }
    
    public static FxArrayNode getArrayOrNull(FxObjNode parent, String fieldName) {
        FxNode fieldNode = parent.get(fieldName);
        if (fieldNode == null) {
            return null;
        }
        if (!fieldNode.isArray()) {
            return null;
        }
        return (FxArrayNode) fieldNode;
    }
    
}
