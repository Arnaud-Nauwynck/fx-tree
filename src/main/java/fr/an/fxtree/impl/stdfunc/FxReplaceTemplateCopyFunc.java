package fr.an.fxtree.impl.stdfunc;

import java.util.Map;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.helper.FxReplaceNodeCopyVisitor;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxReplaceTemplateCopyFunc extends FxNodeFunc {
    
    private FxNode template;
    
    public FxReplaceTemplateCopyFunc(FxNode template) {
        this.template = template;
    }
    
    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        FxObjNode params = (FxObjNode) srcObj.get("params");
        if (params != null) {
            Map<String, FxNode> varReplacements = params.fieldsHashMapCopy();
            FxReplaceNodeCopyVisitor.copyWithReplaceTo(dest, template, varReplacements);
        } else {
            FxNodeCopyVisitor.copyTo(dest, template);
        }
    }
}