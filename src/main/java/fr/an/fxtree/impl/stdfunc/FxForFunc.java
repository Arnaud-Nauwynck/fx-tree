package fr.an.fxtree.impl.stdfunc;

import java.util.HashMap;
import java.util.Map;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildWriter;
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
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        
        int startIndex = FxCurrEvalCtxUtil.recurseEvalToIntOrDefault(ctx, srcObj.get("start"), 0);
        int incr = FxCurrEvalCtxUtil.recurseEvalToIntOrDefault(ctx, srcObj.get("incr"), 1);
        int endIndex = FxCurrEvalCtxUtil.recurseEvalToIntOrThrow(ctx, srcObj.get("end"));
        
        String iterIndexName = FxNodeValueUtils.getOrDefault(srcObj, "indexName", "index");
        FxNode templateNode = srcObj.get("template");

        if (incr == 0 || (incr > 0 && endIndex < startIndex) || (incr < 0 && endIndex > startIndex)) {
            throw new IllegalArgumentException(); 
        }
        if (templateNode == null) {
            return;
        }
        
        FxArrayNode res = dest.addArray();
        FxChildWriter resChildAdder = res.insertBuilder();
                
        FxMemRootDocument tmpDoc = new FxMemRootDocument(); 
        FxObjNode tmpObj = tmpDoc.setContentObj();
        FxIntNode tmpIndexNode = tmpObj.put(iterIndexName, 0);
        
        FxEvalContext childCtx = ctx.createChildContext();
        Map<String,FxNode> replVars = new HashMap<String,FxNode>();
        replVars.put(iterIndexName, tmpIndexNode);

        FxVarsReplaceFunc replaceVarsFunc = new FxVarsReplaceFunc(replVars);

        for(int index = startIndex; ((incr > 0)? (index < endIndex) : (index > endIndex)); index+=incr) {
            tmpIndexNode.setValue(index);
            // set iter value,index in child context
            childCtx.putVariable(iterIndexName, index);
            
            replaceVarsFunc.eval(resChildAdder, childCtx, templateNode);
        }
    }
    
}
