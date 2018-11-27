package fr.an.fxtree.impl.helper;

import fr.an.fxtree.impl.stdfunc.FxCurrEvalCtxUtil;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.path.FxNodeOuterPath;

public class FxNodePerformCopyDeclsVisitor extends FxDefaultStackTreeVisitor {
    
    private final String declMarkerFieldname;
    
    private FxEvalContext ctx;
    
    // ------------------------------------------------------------------------

    public FxNodePerformCopyDeclsVisitor(FxEvalContext ctx, String declMarkerFieldname) {
        this.ctx = ctx;
        this.declMarkerFieldname = declMarkerFieldname;
    }
    
    // ------------------------------------------------------------------------

    public static void recursivePerformCopyDeclsOn(FxNode node,
            FxEvalContext ctx,
            String declMarkerFieldname) {
        if (node == null) {
            return;
        }
        FxNodePerformCopyDeclsVisitor v = new FxNodePerformCopyDeclsVisitor(ctx, declMarkerFieldname);
        v.currentNodeStack.add(node);
        node.accept(v);
    }

    // ------------------------------------------------------------------------

    @Override
    public void visitObj(FxObjNode src) {
        FxNode declNode = src.get(declMarkerFieldname);
        if (declNode == null) {
            super.visitObj(src);
        } else {
            // perform copy declaration, using currPath to resolve extended path outer
            FxObjNode declObjNode = (FxObjNode) declNode;
            FxNodeOuterPath fromExtPath = FxCurrEvalCtxUtil.recurseEvalToOuterPath(ctx, declObjNode.get("fromExtPath"));
            FxNodeOuterPath toExtPath = FxCurrEvalCtxUtil.recurseEvalToOuterPath(ctx, declObjNode.get("toExtPath"));
            
            FxNode fromNode = fromExtPath.selectFromStack(currentNodeStack);
            if (fromNode == null) {
                throw new IllegalStateException("can not find src node to copy using fromExtPath: " + fromExtPath);
            }
            FxChildWriter toNodeWriter = toExtPath.selectInsertBuilderFromStack(currentNodeStack);
            if (toNodeWriter == null) {
                throw new IllegalStateException("can not find destination node to copy using toExtPath: " + toExtPath);
            }

            FxNodeCopyVisitor.copyTo(toNodeWriter, fromNode);
            
            // no recurse ... on the contrary: suppress this performed node!
            // super.visitObj(src);
            src.remove(declMarkerFieldname);
        }
        
    }
    
}
