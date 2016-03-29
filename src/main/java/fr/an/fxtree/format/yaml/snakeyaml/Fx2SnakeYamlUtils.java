package fr.an.fxtree.format.yaml.snakeyaml;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import fr.an.fxtree.format.memmaplist.Fx2MemMapListUtils;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

public final class Fx2SnakeYamlUtils {

    private Fx2SnakeYamlUtils() {
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

    public static void writeTree(OutputStream dest, FxNode tree) throws IOException {
        Object data = buildSnakeYamlTree(tree);
        Yaml yaml = new Yaml();
        try (Writer writer = new OutputStreamWriter(dest)) {
            yaml.dump(data, writer);
        }
    }
    
    // Conversion in-memory (Map,List,Values...) -> FxNode 
    // ------------------------------------------------------------------------
    
    public static FxNode yamlObjToTree(FxChildWriter dest, Object srcObj) {
        return Fx2MemMapListUtils.objectToFxTree(dest, srcObj);
    }

    // Conversion FxNode -> SnakeYaml (Map,List,Values...)  
    // ------------------------------------------------------------------------

    public static Object buildSnakeYamlTree(FxNode src) {
        return Fx2MemMapListUtils.fxTreeToObject(src);
    }
    
}
