package fr.an.fxtree.model.func;

import java.util.Map;

public class FxNodeFuncRegistry {

    private FxNodeFuncRegistry parent;
    private Map<String,FxNodeFunc> funcs;
    
    // ------------------------------------------------------------------------

    public FxNodeFuncRegistry(Map<String,FxNodeFunc> funcs) {
        this(null, funcs);
    }
    
    public FxNodeFuncRegistry(FxNodeFuncRegistry parent, Map<String,FxNodeFunc> funcs) {
        this.parent = parent;
        this.funcs = funcs; 
    }

    // ------------------------------------------------------------------------

    public void registerFunc(String name, FxNodeFunc func) {
        funcs.put(name, func);
    }

    public void unregisterFunc(String name) {
        funcs.remove(name);
    }

    public FxNodeFunc lookupFunction(String name) {
        FxNodeFunc res = funcs.get(name);
        if (res == null && parent != null) {
            res = parent.lookupFunction(name);
        }
        return res;
    }
    
    public static FxNodeFunc lookupFunction(String name, FxNodeFuncRegistry reg, FxNodeFuncRegistry parent) {
        FxNodeFunc res = null;
        if (reg != null) {
            res = reg.lookupFunction(name);
        }
        if (res == null && parent != null) {
            res = parent.lookupFunction(name);
        }
        return res;
    }
    
}
