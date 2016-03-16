package fr.an.fxtree.format.yaml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.util.FxUtils;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public final class FxYamlUtils {
    
    private FxYamlUtils() {
    }

    public static FxNode readTree(InputStream in) {
        FxMemRootDocument doc = new FxMemRootDocument();
        readTree(doc.contentWriter(), in);
        return doc.getContent();
    }

    public static FxNode readTree(File in) {
        FxMemRootDocument doc = new FxMemRootDocument();
        readTree(doc.contentWriter(), in);
        return doc.getContent();
    }
    
    public static FxNode readTree(FxChildWriter dest, InputStream in) {
        Yaml yaml = new Yaml();
        Object yamlObj = yaml.load(in);
        return yamlObjToTree(dest, yamlObj);
    }

    public static FxNode readTree(FxChildWriter dest, File inputFile) {
        if (!inputFile.exists() || !inputFile.isFile()) {
            throw new RuntimeException("Can not read read yaml, no file at '" + inputFile + "'");
        }
        Yaml yaml = new Yaml();
        Object yamlObj;
        try(InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
            yamlObj = yaml.load(in);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read yaml file " + inputFile, ex);
        }
        return yamlObjToTree(dest, yamlObj);
    }

    @SuppressWarnings("unchecked")
    protected static FxNode yamlObjToTree(FxChildWriter dest, Object srcObj) {
        FxNode res;
        if (srcObj == null) {
            res = dest.addNull();
        } else if (srcObj instanceof Map) {
            FxObjNode destObj = dest.addObj();
            recursiveMapToObjTree(destObj, (Map<Object,Object>)srcObj);
            res = destObj;
        } else if (srcObj instanceof Collection) {
            FxArrayNode destArray = dest.addArray();
            recursiveListToArrayTree(destArray, (Collection<Object>)srcObj);
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

    private static void recursiveListToArrayTree(FxArrayNode destArray, Collection<?> srcList) {
        FxChildWriter destEltWriter = destArray.insertBuilder();
        for(Object srcElt : srcList) {
            // recurse
            yamlObjToTree(destEltWriter, srcElt);
        }
    }

    private static void recursiveMapToObjTree(FxObjNode dest, Map<Object,Object> src) {
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
            yamlObjToTree(destValueWriter, srcValue);
        }
    }

}
