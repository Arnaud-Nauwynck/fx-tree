package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildAdder;
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
        FxArrayNode iterValueName = FxNodeValueUtils.getArrayOrNull(srcObj, "value");
        String iterIndexName = FxNodeValueUtils.getOrDefault(srcObj, "indexName", "index");
        FxNode templateNode = srcObj.get("template");
        if (srcValues == null || srcValues.isEmpty() || templateNode == null) {
            return null;
        }
        
        FxArrayNode res = dest.addArray();
        FxChildAdder resChildAdder = res.insertBuilder();
        
        FxEvalContext childEvalContext = ctx.createChildContext();
        FxNodeCopyVisitor copyReplaceIterVisitor = new FxNodeCopyVisitor(); // TODO NOT IMPLEMENTED YET ... should use ReplaceCopyVisitor...
        
        int len = srcValues.size();
        for(int index = 0; index < len; index++) {
            FxNode srcValue = srcValues.get(index);
            
            // set iter value,index in child context
            childEvalContext.putVariable(iterIndexName, index);
            childEvalContext.putVariable(iterValueName, srcValue);
            
            templateNode.accept(copyReplaceIterVisitor, resChildAdder);
        }
        
        return res;
    }
    
}
