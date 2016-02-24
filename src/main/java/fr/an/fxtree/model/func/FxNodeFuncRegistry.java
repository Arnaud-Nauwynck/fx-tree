package fr.an.fxtree.model.func;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import fr.an.fxtree.model.FxChildAdder;
import fr.an.fxtree.model.FxNode;

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

    public FxNode eval(String funcName, FxChildAdder dest, FxNode src) {
        FxNodeFunc func = funcs.get(funcName);
        if (func == null) {
            dest.add("@ERROR Failed to eval: func '" + funcName + "' not found");
            return null;
        }
        return func.eval(dest, src);
    }
    
}
