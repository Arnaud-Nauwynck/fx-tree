package fr.an.fxtree.impl.stdfunc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.json.FxJsonUtils;
import fr.an.fxtree.model.FxArrayNode;
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
        dest.put(FxScanDirImportJsonFilesFunc.NAME, FxScanDirImportJsonFilesFunc.INSTANCE);
        
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
        public static final String NAME = "file.importJsonFile";
        public static final FxImportJsonFileFunc INSTANCE = new FxImportJsonFileFunc();
        
        @Override
        public FxNode eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String fileName = FxNodeValueUtils.getStringOrThrow(srcObj, "filename");
            boolean ignoreFileNotFound = FxNodeValueUtils.getBooleanOrDefault(srcObj, "ignoreFileNotFound", false);
            
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
     * FxFunction to load and parse multiple json files, using scaning filenames<br/>
     * Example usage:
     *<PRE>
     * {                                                   [ 
     *  "@fx-eval": "#phase:scanDirImportJsonFiles"  ==>     jsonFile1,  {}, [], numeric, boolean, text ... whatever type 
     *  "scanDir": "./some/dir",                             jsonfile2,
     *  "recursive": true                                    ..
     *  "jsonFilenamePattern": "*.json"
     * }                                                   ]
     *</PRE> 
     *
     */
    public static class FxScanDirImportJsonFilesFunc extends FxNodeFunc {
        public static final String NAME = "file.scanDirImportJsonFiles";
        public static final FxScanDirImportJsonFilesFunc INSTANCE = new FxScanDirImportJsonFilesFunc();
        
        private static final Logger LOG = LoggerFactory.getLogger(FxStdFileFuncs.FxScanDirImportJsonFilesFunc.class);
        
        @Override
        public FxNode eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String scanDir = FxNodeValueUtils.getStringOrThrow(srcObj, "scanDir");
            boolean recursive = FxNodeValueUtils.getBooleanOrDefault(srcObj, "recursive", true);
            String jsonFilenamePattern = FxNodeValueUtils.getOrDefault(srcObj, "jsonFilenamePattern", ".*\\.json");
            boolean continueOnError = FxNodeValueUtils.getBooleanOrDefault(srcObj, "continueOnError", false);
            
            File file = new File(scanDir);
            if (! file.exists()) {
                throw new IllegalArgumentException(new FileNotFoundException(scanDir));
            }
            if (! file.isDirectory()) {
                throw new IllegalArgumentException("expecting directory to scan, got file '" + file + "'");
            }

            Pattern pattern = Pattern.compile(jsonFilenamePattern);
            
            FxArrayNode destArray = dest.addArray();
            FxChildWriter childWriter = destArray.insertBuilder();
            if (! recursive) {
                for(File f : file.listFiles()) {
                    if (! f.isFile() || ! pattern.matcher(f.getName()).matches()) {
                        continue;
                    }
                    addImportFile(childWriter, file, continueOnError);
                }
            } else {
                recursiveScanDir(file, childWriter, pattern, continueOnError);
            }

            FxJsonUtils.readTree(dest, file);
            return destArray;
        }
        
        protected void recursiveScanDir(File currDir, FxChildWriter childWriter, Pattern pattern, boolean continueOnError) {
            for(File f : currDir.listFiles()) {
                if (f.isDirectory()) {
                    recursiveScanDir(f, childWriter, pattern, continueOnError);
                } else {
                    if (! pattern.matcher(f.getName()).matches()) {
                        continue;
                    }
                    addImportFile(childWriter, f, continueOnError);
                }
            }
        }
        
        protected void addImportFile(FxChildWriter childWriter, File file, boolean continueOnError) {
            try {
                FxJsonUtils.readTree(childWriter, file);
            } catch(Exception ex) {
                if (continueOnError) {
                    LOG.warn("Failed to parse json file '" + file + "', continueOnError=true .. no rethrow!", ex);
                    // ignore, no rethrow!!
                } else {
                    throw new IllegalStateException("Failed to parse json file '" + file + "'", ex);
                }
            }
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
        public static final String NAME = "file.exportJsonFile";
        public static final FxExportJsonFileVoidFunc INSTANCE = new FxExportJsonFileVoidFunc();
        
        @Override
        public FxNode eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            FxNode content = srcObj.get("content");
            String fileName = FxNodeValueUtils.getStringOrThrow(srcObj, "filename");

            File file = new File(fileName);
            File dir = file.getParentFile();
            if (! dir.exists()) {
                boolean createDirIfNotExist = FxNodeValueUtils.getBooleanOrDefault(srcObj, "createDirIfNotExist", false);
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
