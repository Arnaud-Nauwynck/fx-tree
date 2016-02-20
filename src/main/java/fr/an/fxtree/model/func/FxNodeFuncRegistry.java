package fr.an.fxtree.model.func;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public class FxNodeFuncRegistry {

    private Map<String,FxNodeFunc> funcs;
    
    // ------------------------------------------------------------------------

    public FxNodeFuncRegistry(Map<String,FxNodeFunc> funcs) {
        this.funcs = (funcs != null)? ImmutableMap.copyOf(funcs) : Collections.emptyMap(); 
    }

    // ------------------------------------------------------------------------

    public FxNodeFunc get(String name) {
        return funcs.get(name);
    }

    public void eval(String funcName, FxNode dest, FxNode src) {
        FxNodeFunc func = funcs.get(funcName);
        if (func == null) {
            if (dest instanceof FxObjNode) {
                FxObjNode destObj = (FxObjNode) dest;
                destObj.put("@ERROR", "Failed to eval: func '" + funcName + "' not found");
            }
            return;
        }
        func.eval(dest, src);
    }
    
}
