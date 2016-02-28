package fr.an.fxtree.impl.stdfunc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.json.FxJsonUtils;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public final class FxStdFileFuncs {

    /** private to force all static */
    private FxStdFileFuncs() {}
    
    public static void registerBuiltinFuncs(Map<String, FxNodeFunc> dest) {
        dest.put(FxImportJsonFileFunc.NAME, FxImportJsonFileFunc.INSTANCE);
        dest.put(FxExportJsonFileVoidFunc.NAME, FxExportJsonFileVoidFunc.INSTANCE);
        
    }
    
    /**
     * FxFunction to load and parse a json file, using its filename<br/>
     * Example usage:
     *<PRE>
     * {                                              [], {}, numeric, boolean, text ...
     *  "@fx-eval": "#phase:importJsonFile"    ==>    json file content, whatever type 
     *  "fileName": "file.json",
     *  "ignoreFileNotFound": true
     * }                                              
     *</PRE> 
     *
     */
    public static class FxImportJsonFileFunc extends FxNodeFunc {
        public static final String NAME = "importJsonFile";
        public static final FxImportJsonFileFunc INSTANCE = new FxImportJsonFileFunc();
        
        @Override
        public FxNode eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String fileName = FxNodeValueUtils.getStringOrThrow(srcObj, "filename");
            boolean ignoreFileNotFound = FxNodeValueUtils.getOrDefault(srcObj, "ignoreFileNotFound", false);
            
            File file = new File(fileName);
            if (! file.exists()) {
                if (ignoreFileNotFound) {
                    return null;
                } else {
                    throw new RuntimeException(new FileNotFoundException(fileName));
                }
            }
            
            return FxJsonUtils.readTree(dest, file);
        }
    }
    
    
    /**
     * FxFunction to export a tree as json file, given its filename<br/>
     * Example usage:
     *<PRE>
     * {                                              
     *  "@fx-eval": "#phase:exportJsonFile"    ==>    void   
     *  "fileName": "file.json",                      + side-effect: file written "file.json" 
     *  "content": { "someJson": [] }                        { "someJson": [] }  
     * }                                              
     *</PRE> 
     *
     */
    public static class FxExportJsonFileVoidFunc extends FxNodeFunc {
        public static final String NAME = "exportJsonFile";
        public static final FxExportJsonFileVoidFunc INSTANCE = new FxExportJsonFileVoidFunc();
        
        @Override
        public FxNode eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            FxNode content = srcObj.get("content");
            String fileName = FxNodeValueUtils.getStringOrThrow(srcObj, "filename");

            File file = new File(fileName);
            File dir = file.getParentFile();
            if (! dir.exists()) {
                boolean createDirIfNotExist = FxNodeValueUtils.getOrDefault(srcObj, "createDirIfNotExist", false);
                if (createDirIfNotExist) {
                    dir.mkdirs();
                } else {
                    throw new RuntimeException("directory not exist for writing " + file);
                }
            }
            
            String contentStr = content.toString();
            try {
                FileUtils.write(file, contentStr);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to write file " + file, ex);
            }
            
            return null;
        }
    }
    
}
