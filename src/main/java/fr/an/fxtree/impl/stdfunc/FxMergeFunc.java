package fr.an.fxtree.impl.stdfunc;

import java.util.Map;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.helper.FxObjValueHelper;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxMergeFunc extends FxNodeFunc {

    public static final String NAME = "merge";

    // ------------------------------------------------------------------------

    public static final FxMergeFunc INSTANCE = new FxMergeFunc();

    private FxMergeFunc() {
    }

    // ------------------------------------------------------------------------

    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
    	FxObjValueHelper srcH = new FxObjValueHelper((FxObjNode) src);
    	// srcH.getArrayOrNull("params");
    	FxObjNode baseNode = srcH.getObjOrThrow("src");
    	FxObjNode mergeNode = srcH.getObjOrThrow("merge");

    	FxObjNode res = dest.addObj();
		baseNode.forEachFields((f,v) -> {
			FxNodeCopyVisitor.copyChildTo(res, f, v);
		});
    	recursiveMergeAddMissing(res, mergeNode.fieldsCopy());
    }

	protected static void recursiveMergeAddMissing(FxObjNode resNode, Map<String,FxNode> remainMissingFields) {
		// scan fields from base, find corresponding value in mergeNode then recurse merge
		resNode.forEachFields((f,v) -> {
			FxNode mergeFieldValue = remainMissingFields.remove(f);
			if (mergeFieldValue != null) {
				// recurse merge field value content
				if (v instanceof FxObjNode) {
					FxObjNode vObj = (FxObjNode) v;
					if (mergeFieldValue instanceof FxObjNode) {
						FxObjNode mergeFieldObj = (FxObjNode) mergeFieldValue;
						// *** recurse ***
						recursiveMergeAddMissing(vObj, mergeFieldObj.fieldsCopy());
					}// else can not merge Object and non-Object!
				} else if (v instanceof FxArrayNode) {
					if (mergeFieldValue instanceof FxArrayNode) {
						// append to array?  (or merge elts by ids?)
						// TODO not implemented yet
					}// else can not merge Object and non-Object!
				} else {
					// primitive value.. not mergeable
				}
			}
		});
		// scan remaining fields from mergeNode, copy to dest
		if (!remainMissingFields.isEmpty()) {
			FxNodeCopyVisitor.copyChildMapTo(resNode, remainMissingFields);
		}
	}

}
