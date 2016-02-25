package fr.an.fxtree.impl.stdfunc;

import java.util.HashMap;
import java.util.Map;

import fr.an.fxtree.model.func.FxNodeFunc;
import fr.an.fxtree.model.func.FxNodeFuncRegistry;

public class FxStdFuncs {

    public static FxNodeFuncRegistry stdFuncRegistry() { 
        Map<String, FxNodeFunc> funcs = new HashMap<String,FxNodeFunc>();
        FxNodeFuncRegistry funcRegistry = new FxNodeFuncRegistry(funcs);
        
        FxStdMathFuncs.registerBuiltinFuncs(funcs);
        funcs.put(FxForeachFunc.NAME, FxForeachFunc.INSTANCE);
        funcs.put(FxUserPhasesProcessFunc.NAME, new FxUserPhasesProcessFunc(funcRegistry)); // chicken and egg dilemna..
        
        return funcRegistry;
    }
}
