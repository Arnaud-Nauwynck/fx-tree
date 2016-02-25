package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.model.FxChildAdder;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.func.FxBindedNodeFuncExpr;
import fr.an.fxtree.model.func.FxConsts;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;
import fr.an.fxtree.model.func.FxNodeFuncRegistry;

/**
 * clone a tree and evaluate recursively all functions for a given phase (~namespace)
 */
public class FxPhaseRecursiveEvalFunc extends FxNodeFunc {

    private String phase;

    private FxNodeFuncRegistry funcRegistry;
    
    // ------------------------------------------------------------------------
    
    public FxPhaseRecursiveEvalFunc(String phase, FxNodeFuncRegistry funcRegistry) {
        this.phase = phase;
        this.funcRegistry = funcRegistry;
    }
    
    // ------------------------------------------------------------------------

    @Override
    public FxNode eval(FxChildAdder dest, FxEvalContext ctx, FxNode src) {
        return src.accept(new InnerVisitor(ctx), dest); 
    }
    
    private class InnerVisitor extends FxNodeCopyVisitor {
        FxEvalContext ctx;
        
        public InnerVisitor(FxEvalContext ctx) {
            this.ctx = ctx;
        }


        /**
         * detect JSon object that are Meta object for fx evaluation
         * 
         * proposed syntax:
         * <PRE>
         *    {
         *      "@fx-eval" : "#<<phase>>:<<function>>[(<<indexParam0>>,..<<indexParamN>>)]"
         *      <<param0>> : ..,
         *      <<paramN>> : ..
         *    }
         * </PRE>
         * when evaluated => object container replaced by function evaluation
         */
        @Override
        public FxNode visitObj(FxObjNode src, FxChildAdder destNode) {
            FxNode fxEvalFieldValue = src.get(FxConsts.FX_EVAL);
            if (fxEvalFieldValue == null) {
                return super.visitObj(src, destNode); 
            }
            
            String fxEvalExprText = fxEvalFieldValue.textValue();
            // detect if expression text start with "<<namespace>>:" otherwise ignore it
            if (fxEvalExprText == null 
                    || ! (fxEvalExprText.startsWith("#" + phase) 
                            && fxEvalExprText.length() > phase.length()+2 
                            && fxEvalExprText.charAt(phase.length() + 1) == ':')) {
                return super.visitObj(src, destNode);
            }
            
            FxNode fxCacheBindedExpr = src.get(FxConsts.FX_BINDED_EXPR);
            if (fxCacheBindedExpr != null && fxCacheBindedExpr.isPojo()) {
                // already analysed + resolved... simply call it!
                Object pojo = ((FxPOJONode) fxCacheBindedExpr).getValue();
                if (pojo != null && pojo instanceof FxBindedNodeFuncExpr) {
                    FxBindedNodeFuncExpr expr = (FxBindedNodeFuncExpr) pojo;
                    // *** eval ****
                    return expr.eval(destNode);
                }
            }
            
            int optIndexOpenParenthesis = fxEvalExprText.indexOf('(', phase.length()+2);
            String funcName = fxEvalExprText.substring(phase.length() + 2, 
                ((optIndexOpenParenthesis == -1)? fxEvalExprText.length() : optIndexOpenParenthesis));
            if (funcName.endsWith(" ")) {
                funcName = funcName.trim();
            }
            // lookup method by name + eval   (may also cache FxBindedNodeFuncExpr..)
            // *** eval ***
            return funcRegistry.eval(funcName, destNode, ctx, src);
        }
        
    }
}
