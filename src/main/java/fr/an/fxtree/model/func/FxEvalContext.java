package fr.an.fxtree.model.func;

import java.util.HashMap;
import java.util.Map;

public class FxEvalContext {

    private FxEvalContext parentContext;

    private Map<Object,Object> variables = new HashMap<Object,Object>();

    private FxNodeFuncRegistry funcRegistry;
    
    // ------------------------------------------------------------------------

    public FxEvalContext(FxEvalContext parentContext, FxNodeFuncRegistry funcRegistry) {
        this.parentContext = parentContext;
        this.funcRegistry = funcRegistry;
    }

    // ------------------------------------------------------------------------
    
    public FxEvalContext createChildContext() {
        return new FxEvalContext(this, funcRegistry);
    }

    public Object lookupVariable(Object key) {
        Object res = variables.get(key);
        if (res == null) {
            if (variables.containsKey(key)) {
                res = null;
            } else if (parentContext != null) {
                res = parentContext.lookupVariable(key);
            }
        }
        return res;        
    }
    
    public Object getVariableOverride(Object key) {
        return variables.get(key);
    }
    
    public void putVariable(Object key, Object value) {
        variables.put(key, value);
    }

    public void putVariableAll(Map<?,?> src) {
        variables.putAll(src);
    }

    public FxNodeFuncRegistry getFuncRegistry() {
        return funcRegistry;
    }

    public void setFuncRegistry(FxNodeFuncRegistry funcRegistry) {
        this.funcRegistry = funcRegistry;
    }

    public FxNodeFunc lookupFunction(String funcName, FxNodeFuncRegistry overrideFuncRegistry) {
        FxNodeFunc res = null;
        if (overrideFuncRegistry != null) {
            res = overrideFuncRegistry.lookupFunction(funcName);
        }
        if (res == null) {
            res = funcRegistry.lookupFunction(funcName);
        }
        return res;
    }

//    public FxNode recursiveEvalCopyTo(FxChildAdder dest, FxNode src) {
//        if (src == null) {
//            return null;
//        }
//        FxNode res;
//        if (recursiveEvalFunc == null) {
//            res = FxNodeCopyVisitor.copyTo(dest, src);
//        } else {
//            res = recursiveEvalFunc.eval(dest, this, src);
//        }
//        return res;
//    }
//    
//    public FxNode recursiveEval(FxNode src) {
//        if (src == null) {
//            return null;
//        }
//        if (recursiveEvalFunc == null) {
//            return src;
//        }
//        FxMemRootDocument doc = new FxMemRootDocument();
//        FxChildAdder adder = doc.contentAdder();
//        
//        FxNode res = recursiveEvalFunc.eval(adder, this, src);
//
//        if (res == null && doc.getContent() != null) {
//            // should not occur...
//            res = doc.getContent();
//        }
//        return res;
//    }
//    
//    public FxNode evalThenRecursiveEvalCopyTo(FxChildAdder dest, FxNodeFunc firstFunc, FxNode src) {
//        if (recursiveEvalFunc == null) {
//            return firstFunc.eval(dest, this, src);
//        }
//        FxNodeFunc compoundFunc = FxFixedCompoundFunc.chainIfNotNull(firstFunc, recursiveEvalFunc);
//        
//        // recursive eval firstfunc + eval + copy to res
//        FxNode res = compoundFunc.eval(dest, this, src);
//
//        return res;
//    }
//
//    public FxNode replaceVarsThenRecursiveEvalCopyTo(FxChildAdder dest, Map<String,FxNode> replVars, FxNode src) {
//        FxVarsReplaceFunc copyReplaceFunc = new FxVarsReplaceFunc(replVars);
//    
//        FxEvalContext childCtx = createChildContext();
//        childCtx.putVariableAll(replVars);
//    
//        // recursive replace+eval+copy to res
//        FxNode res = childCtx.evalThenRecursiveEvalCopyTo(dest, copyReplaceFunc, src);
//        return res;
//    }
    
}
