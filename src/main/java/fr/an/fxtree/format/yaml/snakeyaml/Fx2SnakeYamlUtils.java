package fr.an.fxtree.format.yaml.snakeyaml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.reader.UnicodeReader;

import fr.an.fxtree.format.memmaplist.Fx2MemMapListUtils;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.path.FxNodePath;

public final class Fx2SnakeYamlUtils {

    private static final Logger LOG = LoggerFactory.getLogger(Fx2SnakeYamlUtils.class);
    
    private Fx2SnakeYamlUtils() {
    }

    public static void writeTree(OutputStream dest, FxNode tree) throws IOException {
        Object data = Fx2MemMapListUtils.treeToValue(tree);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
        
        Yaml yaml = new Yaml(dumperOptions);
        try (Writer writer = new OutputStreamWriter(dest)) {
            yaml.dump(data, writer);
        }
    }
    
    public static FxNode readTree(FxChildWriter dest, InputStream in, FxSourceLoc loc) {
        Yaml yaml = new Yaml();

        Reader reader = new UnicodeReader(in); // remove BOM if any "<U+FEFF>" + use UTF8 or UTF16 or , ....
        Node rootNode = yaml.compose(reader);
        
        return valueToTree(dest, rootNode, loc);
    }

    public static FxNode readTree(FxChildWriter dest, File inputFile, FxSourceLoc loc) {
        if (!inputFile.exists() || !inputFile.isFile()) {
            throw new RuntimeException("Can not read read yaml, no file at '" + inputFile + "'");
        }
        try(InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
            return readTree(dest, in, loc);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read yaml file " + inputFile, ex);
        }
    }

    public static <T> T treeToValue(InputStream in, Class<T> clss) {
        Yaml yaml = new Yaml();
        return yaml.loadAs(in, clss);
    }

    // NOT implemented
//    public static <T> T treeToValue(Node node, Class<T> clss) {
//        new Constructor(composer).getSingleData(clss);
//    }

    public static <T> FxNode valueToTree(T value) {
        Yaml yaml = new Yaml();
        Node tmpres = yaml.represent(value);
        FxSourceLoc loc = FxSourceLoc.inMem();
        FxMemRootDocument doc = new FxMemRootDocument(loc); 
        return valueToTree(doc.contentWriter(), tmpres, loc);
    }

    public static <T> String valueToYamlText(T value) {
    	DumperOptions dumpOpts = new DumperOptions();
    	dumpOpts.setDefaultFlowStyle(FlowStyle.BLOCK);
    	
    	Yaml yaml = new Yaml(dumpOpts);
        return yaml.dump(value);
    }
    
    // ------------------------------------------------------------------------
    
    private static final Pattern INT_PATTERN = Pattern.compile("[+-]?[1-9][0-9]*");
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[+-]?[1-9][0-9]*\\.[0-9]*(e[+-][0-9]+)?");
    
    private static FxNode valueToTree(FxChildWriter dest, Node srcObj, FxSourceLoc loc) {
        FxNode res;
        if (srcObj == null) {
            res = dest.addNull(loc);
        } else if (srcObj instanceof MappingNode) {
            FxObjNode destObj = dest.addObj(loc);
            fillKeyValuesToTree(destObj, (MappingNode)srcObj, loc);
            res = destObj;
        } else if (srcObj instanceof SequenceNode) {
            FxArrayNode destArray = dest.addArray(loc);
            fillValuesToTree(destArray, (SequenceNode)srcObj, loc);
            res = destArray;
            
        } else if (srcObj instanceof ScalarNode) {
            ScalarNode srcScalar = (ScalarNode) srcObj;
            Character style = srcScalar.getStyle(); // '', '\'', '"', '|', '>'
            String value = srcScalar.getValue();
            if (style == '\'' ||  style == '"' ||  style == '|' ||  style == '>') {
                // text
                res = dest.add(value, loc);
            } else {
                // maybe text or boolean,int,float,date,..
                if (value.equalsIgnoreCase("true")) {
                    res = dest.add(true, loc);
                } else if (value.equalsIgnoreCase("false")) {
                    res = dest.add(false, loc);
                } else {
                    if (INT_PATTERN.matcher(value).matches()) {
                        // Integer,Long,BigInteger
                        BigInteger valBigInt = new BigInteger(value);
                        try {
                            int valInt = valBigInt.intValueExact();
                            res = dest.add(valInt, loc);
                        } catch(ArithmeticException ex) {
                            try {
                                long valLong = valBigInt.longValueExact();
                                res = dest.add(valLong, loc);
                            } catch(ArithmeticException ex2) {
                                res = dest.add(valBigInt, loc);
                            }
                        }
                    } else if (FLOAT_PATTERN.matcher(value).matches()){
                        try {
                            double valDouble = Double.parseDouble(value);
                            res = dest.add(valDouble, loc);
                        } catch(Exception ex) {
                            BigDecimal bigDec = new BigDecimal(value);
                            res = dest.add(bigDec, loc);
                        }
                    } else {
                        // current implementation... use text !!! 
                        // may check for Date?
                        res = dest.add(value, loc);
                    }
                }
            }

        } else if (srcObj instanceof AnchorNode) {
            // TODO unsupported ??
            res = null;
        } else {
            // fail-through (unrecognized type?) => use POJO ..
            res = dest.addPOJO(srcObj, loc);
        }
        
        if (res != null) {
            String baseDir = null;
            String filePath = null;
            if (loc != null) {
                baseDir = loc.getBaseDir();
                filePath = loc.getFilePath();
            }
            Mark startMark = srcObj.getStartMark();
            Mark endMark = srcObj.getEndMark();
            FxSourceLoc resLoc = new FxSourceLoc(baseDir, filePath,
                    (startMark != null)? startMark.getLine() + 1: 0, (startMark != null)? startMark.getColumn() + 1: 0,
                    (endMark != null)? endMark.getLine() + 1: 0, (endMark != null)? endMark.getColumn() + 1: 0);
            res.setSourceLoc(resLoc);
        }
        return res;
    }

    private static void fillValuesToTree(FxArrayNode destArray, SequenceNode srcList, FxSourceLoc loc) {
        FxChildWriter destEltWriter = destArray.insertBuilder();
        for(Node srcElt : srcList.getValue()) {
            // recurse
            valueToTree(destEltWriter, srcElt, loc);
        }
    }

    private static void fillKeyValuesToTree(FxObjNode dest, MappingNode src, FxSourceLoc loc) {
        for(NodeTuple e : src.getValue()) {
            Node srcKey = e.getKeyNode();
            Node srcValue = e.getValueNode();
            
            String keyText;
            if (srcKey instanceof ScalarNode) {
                keyText = ((ScalarNode) srcKey).getValue();
            } else {
                FxNodePath currDestPath = FxNodeValueUtils.nodeToAncestorPath(dest);
                String currPath = currDestPath.toString();
                String err = "unrecognised object key, expecting String 'key: value', got key:'" + srcKey + "'"
                        + " at '" + currPath + "'";
                if (srcKey instanceof Map) {
                    err += " ... maybe you put 'key: {{value}}' but '{ }' is a special object un yaml, to be escaped with ' '";
                }
                throw new IllegalArgumentException(err);
            }
            if (srcValue == null) {
                FxNodePath currDestPath = FxNodeValueUtils.nodeToAncestorPath(dest);
                String currPath = currDestPath.toString();
                LOG.warn("Detected incorrect yaml, null value for key: '" + keyText + "'"
                        + " replaced with empty string ''"
                        + " at '" + currPath + "'");                
                FxChildWriter destValueWriter = dest.putBuilder(keyText);
                destValueWriter.add("", loc);
                continue;
            }
            
            FxChildWriter destValueWriter = dest.putBuilder(keyText);
            // recurse
            valueToTree(destValueWriter, srcValue, loc);
        }
    }
}
