package fr.an.fxtree.model.path;

import java.util.List;

import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
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

    /**
     * select child node for this path when starting from <code>src</code> node
     * @param src
     * @return
     */
    public FxNode selectFromStack(List<FxNode> currStackNode) {
        FxNode parentNode = currStackNode.get(currStackNode.size() - parentCount - 1);
        return thenPath.select(parentNode);
    }
    
    public FxChildWriter selectInsertBuilderFromStack(List<FxNode> currStackNode) {
        FxNode parentNode = currStackNode.get(currStackNode.size() - parentCount - 1);
        return thenPath.selectInsertBuilder(parentNode);
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
