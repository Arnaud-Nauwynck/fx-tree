package fr.an.fxtree.format;

import java.io.File;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import fr.an.fxtree.format.json.FxJsonUtils;
import fr.an.fxtree.format.yaml.FxYamlUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

/**
 * File read utility method, delegate to Yaml/Json class using file name extension ".yaml", ".json", .. 
 */
public final class FxFileUtils {

    public static final String JSON_EXT = ".json";
    public static final String YAML_EXT = ".yaml";
    public static final String YML_EXT = ".yml";
    public static final Set<String> STD_FILE_EXTENSIONS = ImmutableSet.of(JSON_EXT, YAML_EXT, YML_EXT);

    /** private to force all static */
    private FxFileUtils() {
    }
    
    public static FxNode readTree(File file, FxSourceLoc loc) {
        FxMemRootDocument doc = new FxMemRootDocument(loc);
        return readTree(doc.contentWriter(), file, loc);
    }
    
    public static FxNode readFirstFileWithSupportedExtension(File dir, String baseFileName) {
        for(String ext : STD_FILE_EXTENSIONS) {
            File file = new File(dir, baseFileName + ext);
            if (file.exists()) {
                FxSourceLoc loc = new FxSourceLoc("", file.getAbsolutePath()); // TODO
                return readTree(file, loc);
            }
        }
        return null;
    }

    public static boolean isSupportedFileExtension(File file) {
        String fileName = file.getName();
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return false;
        }
        String extension = fileName.substring(lastDot+1, fileName.length());
        return isSupportedFileExtension(extension);
    }
    
    public static boolean isSupportedFileExtension(String extension) {
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        return STD_FILE_EXTENSIONS.contains(extension);
    }
    
    public static FxNode readTree(FxChildWriter dest, File file, FxSourceLoc source) {
        String fileName = file.getName();
        FxNode contentNode; 
        if (fileName.endsWith(JSON_EXT)) {
            contentNode = FxJsonUtils.readTree(dest, file, source);
        } else if (fileName.endsWith(YML_EXT) || fileName.endsWith(YAML_EXT)) {
            contentNode = FxYamlUtils.readTree(dest, file, source);
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
