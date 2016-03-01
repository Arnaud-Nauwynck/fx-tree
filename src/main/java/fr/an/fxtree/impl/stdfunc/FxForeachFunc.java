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

public class FxForeachFunc extends FxNodeFunc {

    public static final String NAME = "foreach";
    
    // ------------------------------------------------------------------------

    public static final FxForeachFunc INSTANCE = new FxForeachFunc();
    
    private FxForeachFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        FxNode srcValuesNode = FxNodeValueUtils.getOrThrow(srcObj, "values");
        String iterValueName = FxNodeValueUtils.getOrDefault(srcObj, "value", "value");
        String iterIndexName = FxNodeValueUtils.getOrDefault(srcObj, "indexName", "index");
        FxNode templateNode = srcObj.get("template");
        if (srcValuesNode == null || templateNode == null) {
            return;
        }
        
        FxArrayNode srcValues = FxCurrEvalCtxUtil.recurseEvalToArray(ctx, srcValuesNode);
        if (srcValues.isEmpty()) {
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
        replVars.put(iterValueName, null); //tmp replaced by real value after

        FxVarsReplaceFunc replaceVarsFunc = new FxVarsReplaceFunc(replVars);
    
        int len = srcValues.size();
        for(int index = 0; index < len; index++) {
            FxNode srcValue = srcValues.get(index);
            
            tmpIndexNode.setValue(index);
            // already done once... replVars.put(iterIndexName, tmpIndexNode);
            replVars.put(iterValueName, srcValue);
            
            // set iter value,index in child context
            childCtx.putVariable(iterIndexName, index);
            childCtx.putVariable(iterValueName, srcValue);
            
            replaceVarsFunc.eval(resChildAdder, ctx, templateNode);
        }
    }
    
}
