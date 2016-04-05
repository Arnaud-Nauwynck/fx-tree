package fr.an.fxtree.impl.stdfunc;

import java.util.List;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
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

    private FxNodeFuncRegistry overrideFuncRegistry;
    
    // ------------------------------------------------------------------------
    
    public FxPhaseRecursiveEvalFunc(String phase, FxNodeFuncRegistry overrideFuncRegistry) {
        this.phase = phase;
        this.overrideFuncRegistry = overrideFuncRegistry;
    }
    
    // ------------------------------------------------------------------------

    public static void evalPhases(FxChildWriter dest, List<String> phases, FxEvalContext ctx, FxNode src, FxNodeFuncRegistry funcRegistry) {
        FxNode currPhaseRes = src;
        final int intermediatePhaseLen = phases.size()- 1;
        for(int i = 0; i < intermediatePhaseLen; i++) {
            String phase = phases.get(i);
            FxMemRootDocument tmpResDoc = new FxMemRootDocument();
            FxChildWriter tmpResAdder = tmpResDoc.contentWriter();
            
            FxPhaseRecursiveEvalFunc phaseFunc = new FxPhaseRecursiveEvalFunc(phase, funcRegistry);
            phaseFunc.eval(tmpResAdder, ctx, currPhaseRes);
            
            currPhaseRes = tmpResDoc.getContent();
        }

        String lastPhase = phases.get(intermediatePhaseLen);
        FxPhaseRecursiveEvalFunc lastPhaseFunc = new FxPhaseRecursiveEvalFunc(lastPhase, funcRegistry);
        lastPhaseFunc.eval(dest, ctx, currPhaseRes);
    }
    
    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxEvalContext childCtx = FxCurrEvalCtxUtil.childEvalCtx(ctx, phase, this);
        
        src.accept(new InnerVisitor(childCtx), dest); 
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
        public FxNode visitObj(FxObjNode src, FxChildWriter destNode) {
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
            FxNodeFunc func = ctx.lookupFunction(funcName, overrideFuncRegistry);
            if (func == null) {
                throw new IllegalArgumentException("function '" + funcName+ "' not found");
            }
            
            
            // step 0... recursive eval function parameters !!!
            // TOADD use eager support for function
            
            // *** step 1: eval current node function ***
            FxMemRootDocument tmpNonRecurseDoc = new FxMemRootDocument();
            FxChildWriter tmpNonRecurseWriter = tmpNonRecurseDoc.contentWriter();
            
            func.eval(tmpNonRecurseWriter, ctx, src);
            
            FxNode tmpres = tmpNonRecurseDoc.getContent();
            if (tmpres == null) {
                return null;
            }

            // step 2: recurse eval same phase (other functions) on return result
            FxNode res = tmpres.accept(this, destNode);
            
//            // if no recurse function in "src"... could write directly to output  
//            func.eval(destNode, ctx, src);
//            FxNode res = destNode.getResultChild();
            
            return res;
        }
        
    }
}
