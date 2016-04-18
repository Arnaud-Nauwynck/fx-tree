package fr.an.fxtree.impl.helper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.path.FxNodeOuterPath;
import fr.an.fxtree.model.path.FxNodePath;

/**
 * helper class, wrapping a FxObjNode, for extracting fields nodes from FxObjNode, 
 * and extracting/converting/type-checking values
 *
 *  see also FxNodeValueUtils
 */
public class FxObjValueHelper {

    protected FxObjNode parent;
    
    // ------------------------------------------------------------------------
    
    public FxObjValueHelper(FxObjNode parent) {
        this.parent = parent;
    }

    // ------------------------------------------------------------------------

    public FxNode getOrThrow(String fieldName) {
        return FxNodeValueUtils.getOrThrow(parent, fieldName);
    }

    public String getOrDefault(String fieldName, String defaultValue) {
        return FxNodeValueUtils.getOrDefault(parent, fieldName, defaultValue);
    }

    public String getString(String fieldName) {
        return FxNodeValueUtils.getString(parent, fieldName);
    }

    public String getStringOrThrow(String fieldName) {
        return FxNodeValueUtils.getStringOrThrow(parent, fieldName);
    }

    public String nodeToString(FxNode fieldNode) {
        return FxNodeValueUtils.nodeToString(fieldNode);
    }

    public String getAsTextOrDefault(String fieldName, String defaultValue) {
        return FxNodeValueUtils.getAsTextOrDefault(parent, fieldName, defaultValue);
    }
    
    public FxObjNode getObjOrThrow(String fieldName) {
        return FxNodeValueUtils.getObjOrThrow(parent, fieldName);
    }

    public FxObjNode getObjOrNull(String fieldName) {
        return FxNodeValueUtils.getObjOrNull(parent, fieldName);
    }

    public FxObjNode nodeToObj(FxNode fieldNode) {
        return FxNodeValueUtils.nodeToObj(fieldNode);
    }

    public boolean getBooleanOrThrow(String fieldName) {
        return FxNodeValueUtils.getBooleanOrThrow(parent, fieldName);
    }

    public boolean getBooleanOrDefault(String fieldName, boolean defaultValue) {
        return FxNodeValueUtils.getBooleanOrDefault(parent, fieldName, defaultValue);
    }

    public boolean nodeToBoolean(FxNode src) {
        return FxNodeValueUtils.nodeToBoolean(src);
    }

    public char getCharOrDefault(String fieldName, char defaultValue) {
        return FxNodeValueUtils.getCharOrDefault(parent, fieldName, defaultValue);
    }

    public char getCharOrThrow(String fieldName) {
        return FxNodeValueUtils.getCharOrThrow(parent, fieldName);
    }

    public char nodeToChar(FxNode fieldNode) {
        return FxNodeValueUtils.nodeToChar(fieldNode);
    }

    public int getOrDefault(String fieldName, int defaultValue) {
        return FxNodeValueUtils.getOrDefault(parent, fieldName, defaultValue);
    }

    public int getInt(String fieldName) {
        return FxNodeValueUtils.getIntOrThrow(parent, fieldName);
    }

    public int getIntOrDefault(String fieldName, int defaultValue) {
        return FxNodeValueUtils.getIntOrDefault(parent, fieldName, defaultValue);
    }

    public int getIntOrThrow(String fieldName) {
        return FxNodeValueUtils.getIntOrThrow(parent, fieldName);
    }

    public int nodeToInt(FxNode fieldNode) {
        return FxNodeValueUtils.nodeToInt(fieldNode);
    }

    public long getLongOrDefault(String fieldName, Long defaultValue) {
        return FxNodeValueUtils.getLongOrDefault(parent, fieldName, defaultValue);
    }

    public long getLongOrThrow(String fieldName) {
        return FxNodeValueUtils.getLongOrThrow(parent, fieldName);
    }

    public long nodeToLong(FxNode fieldNode) {
        return FxNodeValueUtils.nodeToLong(fieldNode);
    }

    public Long getLongOrNull(String fieldName) {
        return FxNodeValueUtils.getLongOrNull(parent, fieldName);
    }

    public Date getDateAsLongOrNull(String fieldName) {
       return FxNodeValueUtils.getDateAsLongOrNull(parent, fieldName);
    }
    
    public double getDoubleOrDefault(String fieldName, double defaultValue) {
        return FxNodeValueUtils.getDoubleOrDefault(parent, fieldName, defaultValue);
    }

    public double getDoubleOrThrow(String fieldName) {
        return FxNodeValueUtils.getDoubleOrThrow(parent, fieldName);
    }

    public double nodeToDouble(FxNode fieldNode) {
        return FxNodeValueUtils.nodeToDouble(fieldNode);
    }

    public float getFloatOrDefault(String fieldName, float defaultValue) {
        return FxNodeValueUtils.getFloatOrDefault(parent, fieldName, defaultValue);
    }

    public float getFloatOrThrow(String fieldName) {
        return FxNodeValueUtils.getFloatOrThrow(parent, fieldName);
    }

    public FxArrayNode getArrayOrThrow(String fieldName) {
        return FxNodeValueUtils.getArrayOrThrow(parent, fieldName);
    }

    public FxArrayNode getArrayOrNull(String fieldName) {
        return FxNodeValueUtils.getArrayOrNull(parent, fieldName);
    }

    public FxArrayNode nodeToArray(FxNode fieldNode) {
        return FxNodeValueUtils.nodeToArray(fieldNode);
    }

    public String[] getStringArrayOrThrow(String fieldName, boolean allowRecurseFlatten) {
        return FxNodeValueUtils.getStringArrayOrThrow(parent, fieldName, allowRecurseFlatten);
    }

    public String[] getStringArrayOrNull(String fieldName, boolean allowRecurseFlatten) {
        return FxNodeValueUtils.getStringArrayOrNull(parent, fieldName, allowRecurseFlatten);
    }

    public List<String> getStringListOrNull(String fieldName, boolean allowRecurseFlatten) {
        return FxNodeValueUtils.getStringListOrNull(parent, fieldName, allowRecurseFlatten);
    }

    public String[] nodeToStringArray(FxNode value, boolean allowRecurseFlatten) {
        return FxNodeValueUtils.nodeToStringArray(value, allowRecurseFlatten);
    }

    public byte[] nodeToByteArray(FxNode src) {
        return FxNodeValueUtils.nodeToByteArray(src);
    }

    public Object nodeToValueForType(FxNode src, Class<?> destType, FxEvalContext evalCtx) {
        return FxNodeValueUtils.nodeToValueForType(src, destType, evalCtx);
    }

    public Object nodeToValueForType(FxNode src, Class<?> destType) {
        return FxNodeValueUtils.nodeToValueForType(src, destType);
    }

    public FxNodePath nodeToPath(FxNode src) {
        return FxNodeValueUtils.nodeToPath(src);
    }

    public FxNodeOuterPath nodeToOuterPath(FxNode src) {
        return FxNodeValueUtils.nodeToOuterPath(src);
    }

    public Object tryExtractId(FxNode src) {
        return FxNodeValueUtils.tryExtractId(src);
    }

    public Map<Object, FxNode> tryIndexEltsByIds(FxArrayNode array) {
        return FxNodeValueUtils.tryIndexEltsByIds(array);
    }
    
}
