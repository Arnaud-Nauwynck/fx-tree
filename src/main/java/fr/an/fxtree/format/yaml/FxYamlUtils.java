package fr.an.fxtree.format.yaml;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import fr.an.fxtree.format.yaml.snakeyaml.Fx2SnakeYamlUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

/**
 * Yaml<->FxTree converter utility
 */
public final class FxYamlUtils {
    
    /** privat eto force all static */
    private FxYamlUtils() {
    }

    public static FxNode readTree(byte[] in, FxSourceLoc loc) {
        ByteArrayInputStream bin = new ByteArrayInputStream(in);
        try {
            return readTree(bin, loc);
        } finally {
            IOUtils.closeQuietly(bin);
        }
    }
    
    public static FxNode readTree(InputStream in, FxSourceLoc loc) {
        FxMemRootDocument doc = new FxMemRootDocument(loc);
        readTree(doc.contentWriter(), in, loc);
        return doc.getContent();
    }

    public static FxNode readTree(File in, FxSourceLoc loc) {
        FxMemRootDocument doc = new FxMemRootDocument(loc);
        readTree(doc.contentWriter(), in, loc);
        return doc.getContent();
    }

    public static FxNode readTree(FxChildWriter dest, InputStream in, FxSourceLoc loc) {
        return Fx2SnakeYamlUtils.readTree(dest, in, loc);
    }

    public static FxNode readTree(FxChildWriter dest, File inputFile, FxSourceLoc loc) {
        return Fx2SnakeYamlUtils.readTree(dest, inputFile, loc);
    }

    public static FxNode yamlTextToTree(String yamlText) {
        FxSourceLoc source = new FxSourceLoc("text", "");
        FxMemRootDocument doc = new FxMemRootDocument(source);
        yamlTextToTree(doc.contentWriter(), yamlText);
        return doc.getContent();
    }
    
    public static FxNode yamlTextToTree(FxChildWriter dest, String yamlText) {
        FxSourceLoc loc = new FxSourceLoc("text", "");
        ByteArrayInputStream in = new ByteArrayInputStream(yamlText.getBytes()); 
        try {
            return Fx2SnakeYamlUtils.readTree(dest, in, loc);
        } finally {
            IOUtils.closeQuietly(in);
        }
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
