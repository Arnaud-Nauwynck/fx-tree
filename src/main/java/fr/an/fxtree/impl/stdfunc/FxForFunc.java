package fr.an.fxtree.impl.stdfunc;

import java.util.HashMap;
import java.util.Map;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildAdder;
import fr.an.fxtree.model.FxIntNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxForFunc extends FxNodeFunc {

    public static final String NAME = "for";
    
    // ------------------------------------------------------------------------

    public static final FxForFunc INSTANCE = new FxForFunc();
    
    private FxForFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public FxNode eval(FxChildAdder dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        int startIndex = FxNodeValueUtils.getOrDefault(srcObj, "start", 0);
        int incr = FxNodeValueUtils.getOrDefault(srcObj, "incr", 1);
        int endIndex = FxNodeValueUtils.getIntOrThrow(srcObj, "end");
        String iterIndexName = FxNodeValueUtils.getOrDefault(srcObj, "indexName", "index");
        FxNode templateNode = srcObj.get("template");

        if (incr == 0 || (incr > 0 && endIndex < startIndex) || (incr < 0 && endIndex > startIndex)) {
            throw new IllegalArgumentException(); 
        }
        if (templateNode == null) {
            return null;
        }
        
        FxArrayNode res = dest.addArray();
        FxChildAdder resChildAdder = res.insertBuilder();
                
        FxMemRootDocument tmpDoc = new FxMemRootDocument(); 
        FxObjNode tmpObj = tmpDoc.setContentObj();
        FxIntNode tmpIndexNode = tmpObj.put(iterIndexName, 0);
        
        FxEvalContext childEvalContext = ctx.createChildContext();
        Map<String,FxNode> replVars = new HashMap<String,FxNode>();
        replVars.put(iterIndexName, tmpIndexNode);
        FxVarsReplaceFunc copyReplaceIterVisitor = new FxVarsReplaceFunc(replVars);
        
        for(int index = startIndex; ((incr > 0)? (index < endIndex) : (index > endIndex)); index+=incr) {
            tmpIndexNode.setValue(index);
            // set iter value,index in child context
            childEvalContext.putVariable(iterIndexName, index);
            
            copyReplaceIterVisitor.eval(resChildAdder, ctx, templateNode);
        }
        
        return res;
    }
    
}
