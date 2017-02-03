package fr.an.fxtree.impl.stdfunc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.format.FxFileUtils;
import fr.an.fxtree.format.json.FxJsonUtils;
import fr.an.fxtree.format.yaml.FxYamlUtils;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
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
        dest.put(FxImportYamlFileFunc.NAME, FxImportYamlFileFunc.INSTANCE);
        dest.put(FxImportFileFunc.NAME, FxImportFileFunc.INSTANCE);

        dest.put(FxExportJsonFileVoidFunc.NAME, FxExportJsonFileVoidFunc.INSTANCE);
        dest.put(FxExportYamlFileVoidFunc.NAME, FxExportYamlFileVoidFunc.INSTANCE);

        dest.put(FxScanDirImportFilesFunc.NAME, FxScanDirImportFilesFunc.INSTANCE);

        dest.put(FxFormatTreeToJsonTextFunc.NAME, FxFormatTreeToJsonTextFunc.INSTANCE);
        dest.put(FxFormatTreeToYamlTextFunc.NAME, FxFormatTreeToYamlTextFunc.INSTANCE);
        dest.put(FxParseJsonTextToTreeFunc.NAME, FxParseJsonTextToTreeFunc.INSTANCE);
        dest.put(FxParseYamlTextToTreeFunc.NAME, FxParseYamlTextToTreeFunc.INSTANCE);

    }

    /**
     * FxFunction to load and parse a json file, using its filename<br/>
     * Example usage:
     *<PRE>
     * {                                              [], {}, numeric, boolean, text ...
     *  "@fx-eval": "#phase:file.importJsonFile" ==>  json file content, whatever type
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
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String fileName = FxNodeValueUtils.getStringOrThrow(srcObj, "filename");
            boolean ignoreFileNotFound = FxNodeValueUtils.getBooleanOrDefault(srcObj, "ignoreFileNotFound", false);

            File file = new File(fileName);
            if (! file.exists()) {
                if (ignoreFileNotFound) {
                    return;
                } else {
                    throw new RuntimeException(new FileNotFoundException(fileName));
                }
            }

            FxJsonUtils.readTree(dest, file);
        }
    }

    /**
     * FxFunction to load and parse a yaml file, using its filename<br/>
     * Example usage:
     *<PRE>
     * {                                              [], {}, numeric, boolean, text ...
     *  "@fx-eval": "#phase:file.importYamlFile" ==>  json file content, whatever type
     *  "fileName": "file.yaml",
     *  "ignoreFileNotFound": true
     * }
     *</PRE>
     *
     */
    public static class FxImportYamlFileFunc extends FxNodeFunc {
        public static final String NAME = "file.importYamlFile";
        public static final FxImportYamlFileFunc INSTANCE = new FxImportYamlFileFunc();

        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String fileName = FxNodeValueUtils.getStringOrThrow(srcObj, "filename");
            boolean ignoreFileNotFound = FxNodeValueUtils.getBooleanOrDefault(srcObj, "ignoreFileNotFound", false);

            File file = new File(fileName);
            if (! file.exists()) {
                if (ignoreFileNotFound) {
                    return;
                } else {
                    throw new RuntimeException(new FileNotFoundException(fileName));
                }
            }

            FxYamlUtils.readTree(dest, file);
        }
    }


    /**
     * FxFunction to load and parse a json/yaml/...  file, wth format based on file extension, using its filename<br/>
     * Example usage:
     *<PRE>
     * {                                               [], {}, numeric, boolean, text ...
     *  "@fx-eval": "#phase:file.importFile"    ==>    json/yaml file content, whatever type
     *  "fileName": "file.json",
     *  "ignoreFileNotFound": true
     * }
     *</PRE>
     *
     */
    public static class FxImportFileFunc extends FxNodeFunc {
        public static final String NAME = "file.importFile";
        public static final FxImportFileFunc INSTANCE = new FxImportFileFunc();

        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String fileName = FxNodeValueUtils.getStringOrThrow(srcObj, "filename");
            boolean ignoreFileNotFound = FxNodeValueUtils.getBooleanOrDefault(srcObj, "ignoreFileNotFound", false);

            File file = new File(fileName);
            if (! file.exists()) {
                if (ignoreFileNotFound) {
                    return;
                } else {
                    throw new RuntimeException(new FileNotFoundException(fileName));
                }
            }

            FxFileUtils.readTree(dest, file);
        }
    }


    /**
     * FxFunction to load and parse multiple json files, using scaning filenames<br/>
     * Example usage:
     *<PRE>
     * {                                                   [
     *  "@fx-eval": "#phase:scanDirImportFiles"  ==>         {}, [], numeric, boolean, text ... whatever type
     *  "scanDir": "./some/dir",                             from jsonFile1, yamlFile2, ..
     *  "recursive": true                                    ..
     *  "jsonFilenamePattern": "(*.json)|(*.yaml)"
     * }                                                   ]
     *</PRE>
     *
     */
    public static class FxScanDirImportFilesFunc extends FxNodeFunc {
        public static final String NAME = "file.scanDirImportJsonFiles";
        public static final FxScanDirImportFilesFunc INSTANCE = new FxScanDirImportFilesFunc();

        private static final Logger LOG = LoggerFactory.getLogger(FxStdFileFuncs.FxScanDirImportFilesFunc.class);

        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String scanDir = FxNodeValueUtils.getStringOrThrow(srcObj, "scanDir");
            boolean recursive = FxNodeValueUtils.getBooleanOrDefault(srcObj, "recursive", true);
            String jsonFilenamePattern = FxNodeValueUtils.getOrDefault(srcObj, "jsonFilenamePattern", "(.*\\.json)|(.*\\.yaml)");
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
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
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

            try {
                FxJsonUtils.writeTree(file, content);
            } catch (RuntimeException ex) {
                throw new RuntimeException("Failed to write file '" + file + "'", ex);
            }
        }
    }


    /**
     * FxFunction to export a tree as yaml file, given its filename<br/>
     * Example usage:
     *<PRE>
     * {
     *  "@fx-eval": "#phase:file.exportYamlFile" ==>  void
     *  "fileName": "file.yaml",                      + side-effect: file written "file.yaml"
     *  "content": { "someTree": [] }                        - "someTree": []
     * }
     *</PRE>
     */
    public static class FxExportYamlFileVoidFunc extends FxNodeFunc {
        public static final String NAME = "file.exportYamlFile";
        public static final FxExportYamlFileVoidFunc INSTANCE = new FxExportYamlFileVoidFunc();

        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
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

            try {
                FxYamlUtils.writeTree(file, content);
            } catch (RuntimeException ex) {
                throw new RuntimeException("Failed to write file '" + file + "'", ex);
            }
        }
    }

    /**
     * FxFunction to format tree to Json text<br/>
     * Example usage:
     *<PRE>
     * {
     *  "@fx-eval": "#phase:tree.formatTreeToJsonText" ==>  "{ \\"someTree\\": [] }"
     *  "content": { "someTree": [] }
     * }
     */
    public static class FxFormatTreeToJsonTextFunc extends FxNodeFunc {
        public static final String NAME = "tree.formatTreeToJsonText";
        public static final FxFormatTreeToJsonTextFunc INSTANCE = new FxFormatTreeToJsonTextFunc();

        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            FxNode content = srcObj.get("content");

            String text = FxJsonUtils.treeToJsonText(content);
            dest.add(text);
        }
    }

    /**
     * FxFunction to format tree to Yaml text<br/>
     * Example usage:
     *<PRE>
     * {
     *  "@fx-eval": "#phase:tree.formatTreeToYamlText" ==>  "- \\"someTree\\": []"
     *  "content": { "someTree": [] }
     * }
     */
    public static class FxFormatTreeToYamlTextFunc extends FxNodeFunc {
        public static final String NAME = "tree.formatTreeToYamlText";
        public static final FxFormatTreeToYamlTextFunc INSTANCE = new FxFormatTreeToYamlTextFunc();

        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            FxNode content = srcObj.get("content");

            String text = FxYamlUtils.treeToYamlText(content);
            dest.add(text);
        }
    }

    /**
     * FxFunction to parse Json text to tree<br/>
     * Example usage:
     *<PRE>
     * {
     *  "@fx-eval": "#phase:tree.parseJsonTextToTree" ==>  { "someTree": [] }
     *  "text": "{ \\"someTree\\": [] }"
     * }
     */
    public static class FxParseJsonTextToTreeFunc extends FxNodeFunc {
        public static final String NAME = "tree.parseJsonTextToTree";
        public static final FxParseJsonTextToTreeFunc INSTANCE = new FxParseJsonTextToTreeFunc();

        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String contentText = FxNodeValueUtils.getString(srcObj, "text");

            FxJsonUtils.jsonTextToTree(dest, contentText);
        }
    }

    /**
     * FxFunction to parse Yaml text to tree<br/>
     * Example usage:
     *<PRE>
     * {
     *  "@fx-eval": "#phase:tree.parseYamlTextToTree" ==>  { "someTree": [] }
     *  "text": "- \\"someTree\\": []"
     * }
     */
    public static class FxParseYamlTextToTreeFunc extends FxNodeFunc {
        public static final String NAME = "tree.parseYamlTextToTree";
        public static final FxParseYamlTextToTreeFunc INSTANCE = new FxParseYamlTextToTreeFunc();

        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String contentText = FxNodeValueUtils.getString(srcObj, "text");

            FxYamlUtils.yamlTextToTree(dest, contentText);
        }
    }

}
