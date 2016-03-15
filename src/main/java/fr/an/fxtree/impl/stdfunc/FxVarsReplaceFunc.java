package fr.an.fxtree.impl.stdfunc;

import java.util.Map;

import fr.an.fxtree.impl.helper.FxReplaceNodeCopyVisitor;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxVarsReplaceFunc extends FxNodeFunc {

    protected FxReplaceNodeCopyVisitor replaceCopyVisitor;
    
    // ------------------------------------------------------------------------
    
    public FxVarsReplaceFunc(Map<String, FxNode> varReplacements) {
        replaceCopyVisitor = new FxReplaceNodeCopyVisitor(varReplacements);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        src.accept(replaceCopyVisitor, dest); 
    }
    
}
