package fr.an.fxtree.impl.stdfunc;

import java.util.Map;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.util.FxUtils;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxStdCtxFuncs {

    /** private to force all static */
    private FxStdCtxFuncs() {}
    
    public static void registerBuiltinFuncs(Map<String, FxNodeFunc> dest) {
        dest.put(FxCtxSetVarFunc.DEFAULT_NAME, FxCtxSetVarFunc.DEFAULT_INSTANCE);
        dest.put(FxCtxSetVarCopyFunc.DEFAULT_NAME, FxCtxSetVarCopyFunc.DEFAULT_INSTANCE);
        dest.put(FxCtxGetVarFunc.DEFAULT_NAME, FxCtxGetVarFunc.DEFAULT_INSTANCE);
        dest.put(FxCtxDefineFonctionAliasFunc.DEFAULT_NAME, FxCtxDefineFonctionAliasFunc.DEFAULT_INSTANCE);
        dest.put(FxCtxUndefineFonctionFunc.DEFAULT_NAME, FxCtxUndefineFonctionFunc.DEFAULT_INSTANCE);
        dest.put(FxCtxDefineMacroFunc.DEFAULT_NAME, FxCtxDefineMacroFunc.DEFAULT_INSTANCE);
        dest.put(FxCtxUndefineMacroFunc.DEFAULT_NAME, FxCtxUndefineMacroFunc.DEFAULT_INSTANCE);
        
    }

    // ------------------------------------------------------------------------

    /**
     * <PRE>
     * {
     *   "@fx-eval": "#phase0:ctx.setVar",      ==>    Void
     *   "varName": "x",
     *   "varValue": 123
     * }
     * </PRE>
     * 
     */
    public static class FxCtxSetVarFunc extends FxNodeFunc {
        public static final String DEFAULT_NAME = "ctx.setVar";
        public static final FxCtxSetVarFunc DEFAULT_INSTANCE = new FxCtxSetVarFunc(); 
        protected FxCtxSetVarFunc() {            
        }
        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            String varName = FxNodeValueUtils.getStringOrThrow((FxObjNode) src, "name");
            FxNode value = FxNodeValueUtils.getOrThrow((FxObjNode) src, "value");
            ctx.putVariable(varName, value);
        }
    }

    /**
     * <PRE>
     * {
     *   "@fx-eval": "#phase0:ctx.setVarCopy",      ==>    Void
     *   "varName": "x",
     *   "value": { ... }
     * }
     * </PRE>
     * 
     */
    public static class FxCtxSetVarCopyFunc extends FxNodeFunc {
        public static final String DEFAULT_NAME = "ctx.setVarCopy";
        public static final FxCtxSetVarCopyFunc DEFAULT_INSTANCE = new FxCtxSetVarCopyFunc(); 
        protected FxCtxSetVarCopyFunc() {            
        }
        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            String varName = FxNodeValueUtils.getStringOrThrow((FxObjNode) src, "name");
            FxNode value = FxNodeValueUtils.getOrThrow((FxObjNode) src, "value");
            FxNode valueCopy = FxNodeCopyVisitor.cloneMemNode(value);
            ctx.putVariable(varName, valueCopy);
        }
    }
    
    /**
     * <PRE>
     * {
     *   "@fx-eval": "#phase0:ctx.getVar",      ==>    {}, [], number .... 
     *   "varName": "x"
     * }
     * </PRE>
     * 
     */
    public static class FxCtxGetVarFunc extends FxNodeFunc {
        public static final String DEFAULT_NAME = "ctx.getVar";
        public static final FxCtxGetVarFunc DEFAULT_INSTANCE = new FxCtxGetVarFunc(); 
        protected FxCtxGetVarFunc() {            
        }
        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            String varName = FxNodeValueUtils.getStringOrThrow((FxObjNode) src, "name");
            Object value = ctx.lookupVariable(varName);
            if (value == null) {
                dest.addNull();
            } else if (value instanceof FxNode) {
                FxNodeCopyVisitor.copyTo(dest, (FxNode) value);
            } else {
                throw FxUtils.notImplYet();
            }
        }
    }

    /**
     * <PRE>
     * {
     *   "@fx-eval": "#phase0:ctx.defineFuncAlias",      ==>    Void
     *   "name": "bar",
     *   "funcName": "foo"
     * }
     * </PRE>
     * 
     */
    public static class FxCtxDefineFonctionAliasFunc extends FxNodeFunc {
        public static final String DEFAULT_NAME = "ctx.defineFuncAlias";
        public static final FxCtxDefineFonctionAliasFunc DEFAULT_INSTANCE = new FxCtxDefineFonctionAliasFunc(); 
        protected FxCtxDefineFonctionAliasFunc() {            
        }
        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String alias = FxNodeValueUtils.getStringOrThrow(srcObj, "name");
            String funcName = FxNodeValueUtils.getStringOrThrow(srcObj, "function");
            FxNodeFunc func = ctx.lookupFunction(funcName, null);
            if (func == null) {
                throw new IllegalArgumentException("Function '" + funcName + "' not found");
            }
            ctx.getFuncRegistry().registerFunc(alias, func);
        }
    }

    /**
     * <PRE>
     * {
     *   "@fx-eval": "#phase0:ctx.undefineFunc",      ==>    Void
     *   "name": "foo"
     * }
     * </PRE>
     * 
     */
    public static class FxCtxUndefineFonctionFunc extends FxNodeFunc {
        public static final String DEFAULT_NAME = "ctx.undefineFunc";
        public static final FxCtxUndefineFonctionFunc DEFAULT_INSTANCE = new FxCtxUndefineFonctionFunc(); 
        protected FxCtxUndefineFonctionFunc() {            
        }
        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String name = FxNodeValueUtils.getStringOrThrow(srcObj, "name");
            ctx.getFuncRegistry().unregisterFunc(name);
        }
    }

    /**
     * <PRE>
     * {
     *   "@fx-eval": "#phase0:ctx.defineMacro",      ==>    Void
     *   "name": "x",
     *   "template": any..
     * }
     * </PRE>
     * 
     */
    public static class FxCtxDefineMacroFunc extends FxNodeFunc {
        public static final String DEFAULT_NAME = "ctx.defineMacro";
        public static final FxCtxDefineMacroFunc DEFAULT_INSTANCE = new FxCtxDefineMacroFunc(); 
        protected FxCtxDefineMacroFunc() {            
        }
        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String name = FxNodeValueUtils.getStringOrThrow(srcObj, "name");
            FxNode template = FxNodeValueUtils.getOrThrow(srcObj, "template");
            FxNode templateCopy = FxNodeCopyVisitor.cloneMemNode(template);
            FxReplaceTemplateCopyFunc macroFunc = new FxReplaceTemplateCopyFunc(templateCopy);
            ctx.getFuncRegistry().registerFunc(name, macroFunc);
        }
    }

    /**
     * <PRE>
     * {
     *   "@fx-eval": "#phase0:ctx.undefineMacro",      ==>    Void
     *   "name": "x"
     * }
     * </PRE>
     * 
     */
    public static class FxCtxUndefineMacroFunc extends FxNodeFunc {
        public static final String DEFAULT_NAME = "ctx.undefineMacro";
        public static final FxCtxUndefineMacroFunc DEFAULT_INSTANCE = new FxCtxUndefineMacroFunc(); 
        protected FxCtxUndefineMacroFunc() {            
        }
        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            String name = FxNodeValueUtils.getStringOrThrow(srcObj, "name");
            ctx.getFuncRegistry().unregisterFunc(name);
        }
    }

}
