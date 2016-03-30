package fr.an.fxtree.format.yaml;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.an.fxtree.format.yaml.snakeyaml.Fx2SnakeYamlUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

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
        return Fx2SnakeYamlUtils.readTree(dest, in);
    }

    public static FxNode readTree(FxChildWriter dest, File inputFile) {
        return Fx2SnakeYamlUtils.readTree(dest, inputFile);
    }

    public static FxNode yamlTextToTree(String yamlText) {
        FxMemRootDocument doc = new FxMemRootDocument();
        yamlTextToTree(doc.contentWriter(), yamlText);
        return doc.getContent();
    }
    
    public static FxNode yamlTextToTree(FxChildWriter dest, String yamlText) {
        ByteArrayInputStream in = new ByteArrayInputStream(yamlText.getBytes()); 
        return Fx2SnakeYamlUtils.readTree(dest, in);
    }
    
    public static void writeTree(OutputStream dest, FxNode tree) throws IOException {
        Fx2SnakeYamlUtils.writeTree(dest, tree);
    }
    
    public static String treeToYamlText(FxNode tree) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            Fx2SnakeYamlUtils.writeTree(bout, tree);
        } catch(IOException ex) {
            throw new RuntimeException("Should not occur: Failed to convert tree to yaml text", ex);
        }
        return bout.toString();
    }
    
    public static void writeTree(File dest, FxNode tree) {
        try (OutputStream output = new BufferedOutputStream(new FileOutputStream(dest))) {
            writeTree(output, tree);
        }
        catch(IOException ex) {
            throw new RuntimeException("Failed to write as yaml to file '" + dest + "'", ex);
        }
    }
}
