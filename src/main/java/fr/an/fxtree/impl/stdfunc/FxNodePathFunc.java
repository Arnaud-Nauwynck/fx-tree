package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxNodePathFunc extends FxNodeFunc {

    public static final String NAME = "nodePath";
    
    // ------------------------------------------------------------------------

    public static final FxNodePathFunc INSTANCE = new FxNodePathFunc();
    
    private FxNodePathFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        String expr = FxCurrEvalCtxUtil.recurseEvalToString(ctx, srcObj.get("expr"));
        FxNode in = FxCurrEvalCtxUtil.recurseEval(ctx, srcObj.get("in"));

        FxNode value = evalSubPathExpr(in, expr);
        
        FxNodeCopyVisitor.copyTo(dest, value);
    }

    public static String evalSubPathExprAsText(FxNode rootNode, String pathExpr) {
        FxNode tmp = evalSubPathExpr(rootNode, pathExpr);
        if (tmp == null) {
            return "";
        }
        return tmp.asText();
    }

    public static FxNode evalSubPathExpr(FxNode rootNode, String pathExpr) {
        // cf also FxJqFunc.evalJqExprAsText(rootNode, jqExpr);
        FxNode curr = rootNode;
        String[] pathElts = pathExpr.split("\\.");
        for(String pathElt : pathElts) {
            if (curr == null) {
                throw new RuntimeException("*** ERROR evaluating path expr " + pathExpr + " : NullPointer");
            }
            if (pathElt.startsWith("[")) {
                // indexed
                if (!pathElt.endsWith("]"))  {
                    throw createEvalEx(rootNode, pathExpr, curr, pathElt,
                            "expecting path elt \"[index]\", got \"" + pathElt + "\"");
                }
                int index = Integer.parseInt(pathElt.substring(1, pathElt.length() - 1));
                if (curr instanceof FxArrayNode) {
                    FxArrayNode curr2 = (FxArrayNode) curr;
                    int arrSize = curr2.size();
                    if (index < 0 || index >= arrSize) {
                        throw createEvalEx(rootNode, pathExpr, curr, pathElt,
                            "OutOfBoundException for indexed elt \"[" + index + "]\", from array length:" + arrSize);
                    }
                    curr = curr2.get(index);
                } else {
                    throw createEvalEx(rootNode, pathExpr, curr, pathElt,
                        "expecting array, got " + curr.getNodeType());
                }
            } else {
                // field
                if (curr instanceof FxObjNode) {
                    FxObjNode curr2 = (FxObjNode) curr;
                    curr = curr2.get(pathElt);
                } else {
                    throw createEvalEx(rootNode, pathExpr, curr, pathElt,
                            "expecting object, got " + curr.getNodeType());
                }
            }
        }
        return curr;
    }

    private static RuntimeException createEvalEx(FxNode rootNode, String pathExpr, 
            FxNode currNode, String pathElt,
            String msg) {
        return new RuntimeException("ERROR evaluating path expr '" + pathExpr + "' " 
                + " field:" + pathElt + " of " + currNode + " : " + msg);
    }
    
}
