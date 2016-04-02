package fr.an.fxtree.format;

import java.io.File;

import fr.an.fxtree.format.json.FxJsonUtils;
import fr.an.fxtree.format.yaml.FxYamlUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

/**
 * File read utility method, delegate to Yaml/Json class using file name extension ".yaml", ".json", .. 
 */
public final class FxFileUtils {

    public static final String JSON_EXT = ".json";
    public static final String YAML_EXT = ".yaml";
    public static final String YML_EXT = ".yml";
    public static final String[] STD_FILE_EXTENSIONS = new String[] { JSON_EXT, YAML_EXT, YML_EXT };

    /** private to force all static */
    private FxFileUtils() {
    }
    
    public static FxNode readTree(File file) {
        FxMemRootDocument doc = new FxMemRootDocument();
        return readTree(doc.contentWriter(), file);
    }
    
    public static boolean isSupportedFileExtension(String extension) {
        return extension.equalsIgnoreCase("json") ||
                extension.equalsIgnoreCase("yaml") || extension.equalsIgnoreCase("yml");
    }
    
    public static FxNode readTree(FxChildWriter dest, File file) {
        String fileName = file.getName();
        FxNode contentNode; 
        if (fileName.endsWith(JSON_EXT)) {
            contentNode = FxJsonUtils.readTree(dest, file);
        } else if (fileName.endsWith(YML_EXT) || fileName.endsWith(YAML_EXT)) {
            contentNode = FxYamlUtils.readTree(dest, file);
        } else {
            throw new IllegalArgumentException("expecting .json or .yaml file extension, got " + fileName);
        }
        return contentNode;
    }

    public static void writeTree(File file, FxNode content) {
        String fileName = file.getName();
        if (fileName.endsWith(JSON_EXT)) {
            FxJsonUtils.writeTree(file, content);
        } else if (fileName.endsWith(YML_EXT) || fileName.endsWith(YAML_EXT)) {
            FxYamlUtils.writeTree(file, content);
        } else {
            throw new IllegalArgumentException("expecting .json or .yaml file extension, got " + fileName);
        }
    }
    
}
