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

}
