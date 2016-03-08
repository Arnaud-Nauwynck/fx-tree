package fr.an.fxtree.format;

import java.io.File;

import fr.an.fxtree.format.json.FxJsonUtils;
import fr.an.fxtree.format.yaml.FxYamlUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

public final class FxFileUtils {

    private FxFileUtils() {
    }
    
    public static FxNode readTree(File file) {
        FxMemRootDocument doc = new FxMemRootDocument();
        return readTree(doc.contentWriter(), file);
    }
    
    public static FxNode readTree(FxChildWriter dest, File file) {
        String fileName = file.getName();
        FxNode contentNode; 
        if (fileName.endsWith(".json")) {
            contentNode = FxJsonUtils.readTree(dest, file);
        } else if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
            contentNode = FxYamlUtils.readTree(dest, file);
        } else {
            throw new IllegalArgumentException("expecting .json or .yaml file extension, got " + fileName);
        }
        return contentNode;
    }
    
}
