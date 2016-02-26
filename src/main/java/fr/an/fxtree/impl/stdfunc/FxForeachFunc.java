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

public class FxForeachFunc extends FxNodeFunc {

    public static final String NAME = "foreach";
    
    // ------------------------------------------------------------------------

    public static final FxForeachFunc INSTANCE = new FxForeachFunc();
    
    private FxForeachFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public FxNode eval(FxChildAdder dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        FxArrayNode srcValues = FxNodeValueUtils.getArrayOrNull(srcObj, "values");
        String iterValueName = FxNodeValueUtils.getOrDefault(srcObj, "value", "value");
        String iterIndexName = FxNodeValueUtils.getOrDefault(srcObj, "indexName", "index");
        FxNode templateNode = srcObj.get("template");
        if (srcValues == null || srcValues.isEmpty() || templateNode == null) {
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
        replVars.put(iterValueName, null);
        // replVars.put(iterValueName, tmpIndexNode); //tmp replaced by real value after
        FxVarsReplaceFunc copyReplaceIterVisitor = new FxVarsReplaceFunc(replVars);

        
        int len = srcValues.size();
        for(int index = 0; index < len; index++) {
            FxNode srcValue = srcValues.get(index);
            
            tmpIndexNode.setValue(index);
            // already done once... replVars.put(iterIndexName, tmpIndexNode);
            replVars.put(iterValueName, srcValue);
            
            // set iter value,index in child context
            childEvalContext.putVariable(iterIndexName, index);
            childEvalContext.putVariable(iterValueName, srcValue);
            
            // templateNode.accept(copyReplaceIterVisitor, resChildAdder);
            copyReplaceIterVisitor.eval(resChildAdder, ctx, templateNode);
            
            // TODO replace templateNode->tmpTemplate.. + recursive eval tmpTemplate->res

        }
        
        return res;
    }
    
}
