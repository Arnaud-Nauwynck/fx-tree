package fr.an.fxtree.model.func;

import java.util.HashMap;
import java.util.Map;

public class FxEvalContext {

    private FxEvalContext parentContext;
    private Map<Object,Object> variables = new HashMap<Object,Object>();

    private FxNodeFunc recursiveEvalFunc;
    
    // ------------------------------------------------------------------------

    public FxEvalContext(FxEvalContext parentContext, FxNodeFunc recursiveEvalFunc) {
        this.parentContext = parentContext;
        this.recursiveEvalFunc = recursiveEvalFunc;
    }

    // ------------------------------------------------------------------------
    
    public FxEvalContext createChildContext() {
        return new FxEvalContext(this, recursiveEvalFunc);
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

    public FxNodeFunc getRecursiveEvalFunc() {
        return recursiveEvalFunc;
    }

    public void setRecursiveEvalFunc(FxNodeFunc recursiveEvalFunc) {
        this.recursiveEvalFunc = recursiveEvalFunc;
    }
    
}
