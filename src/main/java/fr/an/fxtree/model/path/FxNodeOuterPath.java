package fr.an.fxtree.model.path;

import fr.an.fxtree.model.path.impl.FxNodePathParserUtils;

/**
 * outer path are extended node path, starting by "^", "^1", ... "^n", then followed by a child path ".a.b[2]"  
 */
public final class FxNodeOuterPath {

    private final int parentCount;
    private final FxNodePath thenPath;

    // ------------------------------------------------------------------------
    
    public FxNodeOuterPath(int parentCount, FxNodePath thenPath) {
        if (parentCount < 0) throw new IllegalArgumentException();
        if (thenPath == null) throw new IllegalArgumentException();
        this.parentCount = parentCount;
        this.thenPath = thenPath;
    }

    public static FxNodeOuterPath of(int parentCount, FxNodePath thenPath) {
        return new FxNodeOuterPath(parentCount, thenPath);
    }

    public static FxNodeOuterPath parse(String extPath) {
        return FxNodePathParserUtils.parseOuterPath(extPath);
    }

    // ------------------------------------------------------------------------
    
    public int getParentCount() {
        return parentCount;
    }

    public FxNodePath getThenPath() {
        return thenPath;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (parentCount != 0) {
            sb.append('^');
            if (parentCount > 1) {
                sb.append(parentCount);
            }
        }
        thenPath.toString(sb);
        return sb.toString();
    }
    
}
