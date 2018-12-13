package fr.an.fxtree.impl.model.mem;

public class FxSourceLoc {

    protected static final FxSourceLoc IN_MEM = new FxSourceLoc("", "");

    public static FxSourceLoc inMem() {
        return IN_MEM;
    }

    private final String baseDir;
    private final String filePath;
    private final int fromLine;
    private final int fromColumn;
    private final int toLine;
    private final int toColumn;
    
    // ------------------------------------------------------------------------
    
    public FxSourceLoc(String baseDir, String filePath) {
        this(baseDir, filePath, 0, 0, 0, 0);
    }
    
    public FxSourceLoc(String baseDir, String filePath, int fromLine, int fromColumn, int toLine, int toColumn) {
        this.baseDir = baseDir;
        this.filePath = filePath;
        this.fromLine = fromLine;
        this.fromColumn = fromColumn;
        this.toLine = toLine;
        this.toColumn = toColumn;
    }

    public static FxSourceLoc newFrom(String transformMessage, FxSourceLoc base) {
        String filePath = transformMessage + ((base != null)? " " + base.getFilePath() : "");
        return new FxSourceLoc(base.getBaseDir(), filePath, base.fromLine, base.fromColumn, base.toLine, base.toColumn);
    }
    
    public static FxSourceLoc newFrom(String transformMessage, FxSourceLoc src1, FxSourceLoc src2) {
        String baseDir = src1.baseDir;
        String filePath = transformMessage 
                + " (left: " + src1.getFilePath()
                + " , right: " + src2.getFilePath() + ")";
        return new FxSourceLoc(baseDir, filePath, 
                src1.fromLine, src1.fromColumn, src1.toLine, src1.toColumn);
    }

    // ------------------------------------------------------------------------
    
    public String getBaseDir() {
        return baseDir;
    }
    
    public String getFilePath() {
        return filePath;
    }

    public int getFromLine() {
        return fromLine;
    }

    public int getFromColumn() {
        return fromColumn;
    }

    public int getToLine() {
        return toLine;
    }

    public int getToColumn() {
        return toColumn;
    }

    @Override
    public String toString() {
        return ((baseDir != null)? "'" + baseDir + "': " : "")
                + ((filePath != null)? "'" + filePath + "'" : "") 
                + " @" + fromLine + ":" + fromColumn
                + "-" + ((toLine != fromLine)? toLine + ":" : "") + toColumn;
    }
    
}
