package fr.an.fxtree.impl.stdfunc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;
import fr.an.fxtree.model.path.FxNodeOuterPath;
import fr.an.fxtree.model.path.FxNodePath;

public final class FxCurrEvalCtxUtil {

    private static final String PROP_CURR_PHASE_NAME = "currPhaseName";
    private static final String PROP_CURR_PHASE_EVAL_FUNC = "currPhaseEvalFunc";

    public static FxEvalContext childEvalCtx(FxEvalContext ctx, String phaseName, FxNodeFunc evalFunc) { 
        FxEvalContext childCtx = ctx.createChildContext();
        childCtx.putVariable(PROP_CURR_PHASE_NAME, phaseName);
        setCurrEvalFunc(childCtx, evalFunc);
        return childCtx;
    }

    public static String currPhaseName(FxEvalContext ctx) {
        return (String) ctx.lookupVariable(PROP_CURR_PHASE_NAME);
    }

    public static FxNodeFunc currPhaseEvalFunc(FxEvalContext ctx) {
        return (FxNodeFunc) ctx.lookupVariable(PROP_CURR_PHASE_EVAL_FUNC);
    }

    public static void setCurrEvalFunc(FxEvalContext childCtx, FxNodeFunc evalFunc) {
        childCtx.putVariable(PROP_CURR_PHASE_EVAL_FUNC, evalFunc);
    }


    public static void recurseEvalTo(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxNodeFunc currFunc = currPhaseEvalFunc(ctx);
        if (currFunc != null) {
            currFunc.eval(dest, ctx, src);
        } else {
            // should not occur: not evaluating any phase (not using this function class)??
            FxNodeCopyVisitor.copyTo(dest, src);
        }
    }
    
    public static FxNode recurseEval(FxEvalContext ctx, FxNode src) {
        FxNodeFunc currFunc = currPhaseEvalFunc(ctx);
        if (currFunc != null) {
            FxMemRootDocument tmpDoc = new FxMemRootDocument(src.getSourceLoc()); 
            recurseEvalTo(tmpDoc.contentWriter(), ctx, src);
            return tmpDoc.getContent();
        } else {
            return src;
        }
    }
        
    public static boolean recurseEvalToBoolean(FxEvalContext ctx, FxNode src) {
        FxNode tmpres = recurseEval(ctx, src);
        return FxNodeValueUtils.nodeToBoolean(tmpres);
    }

    public static boolean recurseEvalToBooleanOrDefault(FxEvalContext ctx, FxNode src, boolean defaultValue) {
        if (src == null) {
            return defaultValue;
        }
        FxNode tmpres = recurseEval(ctx, src);
        if (tmpres == null) {
            return defaultValue;
        }
        return FxNodeValueUtils.nodeToBoolean(tmpres);
    }
    
    public static String recurseEvalToString(FxEvalContext ctx, FxNode src) {
        FxNode tmpres = recurseEval(ctx, src);
        return FxNodeValueUtils.nodeToString(tmpres);
    }

    public static String recurseEvalToStringOrDefault(FxEvalContext ctx, FxNode src, String defaultValue) {
        if (src == null) {
            return defaultValue;
        }
        FxNode tmpres = recurseEval(ctx, src);
        if (tmpres == null) {
            return defaultValue;
        }
        return FxNodeValueUtils.nodeToString(tmpres);
    }

    public static int recurseEvalToInt(FxEvalContext ctx, FxNode src) {
        FxNode tmpres = recurseEval(ctx, src);
        return FxNodeValueUtils.nodeToInt(tmpres);
    }

    public static int recurseEvalToIntOrDefault(FxEvalContext ctx, FxNode src, int defaultValue) {
        if (src == null) {
            return defaultValue;
        }
        FxNode tmpres = recurseEval(ctx, src);
        if (tmpres == null) {
            return defaultValue;
        }
        return FxNodeValueUtils.nodeToInt(tmpres);
    }

    public static int recurseEvalToIntOrThrow(FxEvalContext ctx, FxNode src) {
        if (src == null) {
            throw new IllegalArgumentException("expected int, got null");
        }
        FxNode tmpres = recurseEval(ctx, src);
        return FxNodeValueUtils.nodeToInt(tmpres);
    }

    public static long recurseEvalToLong(FxEvalContext ctx, FxNode src) {
        FxNode tmpres = recurseEval(ctx, src);
        return FxNodeValueUtils.nodeToLong(tmpres);
    }

    public static long recurseEvalToLongOrDefault(FxEvalContext ctx, FxNode src, Long defaultValue) {
        if (src == null) {
            return defaultValue;
        }
        FxNode tmpres = recurseEval(ctx, src);
        if (tmpres == null) {
            return defaultValue;
        }
        return FxNodeValueUtils.nodeToLong(tmpres);
    }

    public static Long recurseEvalToLongOrThrow(FxEvalContext ctx, FxNode src) {
        if (src == null) {
            throw new IllegalArgumentException("expected long, got null");
        }
        FxNode tmpres = recurseEval(ctx, src);
        return FxNodeValueUtils.nodeToLong(tmpres);
    }
    
    public static FxObjNode recurseEvalToObj(FxEvalContext ctx, FxNode src) {
        FxNode tmpres = recurseEval(ctx, src);
        return FxNodeValueUtils.nodeToObj(tmpres);
    }

    public static Map<String,FxNode> recurseEvalToNamedNodes(FxEvalContext ctx, FxNode src) {
        FxObjNode tmpres = recurseEvalToObj(ctx, src);
        Map<String,FxNode> res = tmpres.fieldsMap();
        return res;
    }

    public static FxArrayNode recurseEvalToArray(FxEvalContext ctx, FxNode src) {
        FxNode tmpres = recurseEval(ctx, src);
        return FxNodeValueUtils.nodeToArray(tmpres);
    }

    public static FxNodePath recurseEvalToPath(FxEvalContext ctx, FxNode src) {
        FxNode tmpres = recurseEval(ctx, src);
        return FxNodeValueUtils.nodeToPath(tmpres);
    }

    public static FxNodeOuterPath recurseEvalToOuterPath(FxEvalContext ctx, FxNode src) {
        FxNode tmpres = recurseEval(ctx, src);
        return FxNodeValueUtils.nodeToOuterPath(tmpres);
    }


    
    
    public static void recurseEvalNestedCtxTo(FxChildWriter dest, FxEvalContext childCtx, FxNode src) {
        List<FxEvalContext> ctxChain = new ArrayList<>();
        for(FxEvalContext curr = childCtx; curr != null; curr = curr.getParentContext()) {
            ctxChain.add(0, curr);
        }
        FxNode res = src;
        for(FxEvalContext ctxElt : ctxChain) {
            res = recurseEval(ctxElt, res);
        }
        FxNodeCopyVisitor.copyTo(dest, res);
    }
}
