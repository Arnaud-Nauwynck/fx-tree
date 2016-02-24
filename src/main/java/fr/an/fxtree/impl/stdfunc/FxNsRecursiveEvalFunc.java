package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxConsts;
import fr.an.fxtree.model.func.FxNodeFunc;
import fr.an.fxtree.model.func.FxNodeFuncRegistry;

/**
 * clone a tree and evaluate recursively all functions for a given namespace/phase
 */
public class FxNsRecursiveEvalFunc extends FxNodeFunc {

    private String namespace;

    private FxNodeFuncRegistry funcRegistry;
    
    // ------------------------------------------------------------------------
    
    public FxNsRecursiveEvalFunc(String namespace, FxNodeFuncRegistry funcRegistry) {
        this.namespace = namespace;
        this.funcRegistry = funcRegistry;
    }
    
    // ------------------------------------------------------------------------

    @Override
    public void eval(FxNode dest, FxNode src) {
        src.accept(new InnerVisitor(), dest); 
    }
    
    private class InnerVisitor extends FxNodeCopyVisitor {

        @Override
        public FxNode visitObj(FxObjNode src, FxNode destNode) {
            FxNode fxEvalProp = src.get(FxConsts.FX_EVAL);
            if (fxEvalProp == null) {
                // clone
                //TODO 
            } else {
                
            }
            return null;
        }

        
    }
}
