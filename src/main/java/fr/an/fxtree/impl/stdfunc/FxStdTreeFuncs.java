package fr.an.fxtree.impl.stdfunc;

import java.util.Map;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;
import fr.an.fxtree.model.path.FxNodePath;

public final class FxStdTreeFuncs {

    /** private to force all static */
    private FxStdTreeFuncs() {}
    
    public static void registerBuiltinFuncs(Map<String, FxNodeFunc> dest) {
        dest.put(FxCopyTreeFunc.NAME, FxCopyTreeFunc.INSTANCE);
        
    }
    
    /**
     * FxFunction to copy tree from one child node to another
     * Example usage:
     *<PRE>
     * {                                         
     *  "@fx-eval": "#phase:tree.copy"    ==>     
     *  from: "shared.srca.srcb",
     *  to: "shared.destb"
     *  body: {                                 {
     *    xx: 123,                                xx: 123,
     *    shared:                                 shared:  
     *      srca: {                                 srca: {
     *        srcb: { bContent: 123},                 srcb: { bContent: 123},
     *        other: 456                              other: 456,
     *      }                                       },
     *    },                                        destb: { 
     *    yy: 456                                     bContent: 123,
     *  }                                           },
     * }                                          },
     *                                            yy: 456
     *                                          }
     *</PRE> 
     *
     */
    public static class FxCopyTreeFunc extends FxNodeFunc {
        public static final String NAME = "tree.copy";
        public static final FxCopyTreeFunc INSTANCE = new FxCopyTreeFunc();
        
        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            FxNode body = srcObj.get("body");
            FxNodePath fromPath = FxCurrEvalCtxUtil.recurseEvalToPath(ctx, srcObj.get("fromPath"));
            FxNodePath toPath = FxCurrEvalCtxUtil.recurseEvalToPath(ctx, srcObj.get("toPath"));

            FxNode destBody = FxNodeCopyVisitor.copyTo(dest, body);

            FxNode copyFromNode = fromPath.select(body);
            if (copyFromNode == null) {
                throw new IllegalArgumentException("can not copy node, fromPath '" + fromPath + "' not found in body"); 
            }
                        
            FxChildWriter toPathWriter = toPath.selectInsertBuilder(destBody);
            FxNodeCopyVisitor.copyTo(toPathWriter, copyFromNode);
        }
    }
    
}
