package fr.an.fxtree.impl.stdfunc;

import java.util.Map;

import fr.an.fxtree.impl.helper.FxNodeCopyMergeVisitor;
import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.model.FxArrayNode;
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
        dest.put(FxMergeTreeFunc.NAME, FxMergeTreeFunc.INSTANCE);
        
    }
    
    /**
     * FxFunction to copy tree from one child path to another child path
     * Example usage:
     *<PRE>
     * {                                         
     *  "@fx-eval": "#phase:tree.copy"    ==>     
     *  fromPath: "shared.srca.srcb",
     *  toPath: "shared.destb"
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
    
    /**
     * FxFunction to merge 2 (or more) trees from child paths to another child path
     * Example usage:
     *<PRE>
{
  "eval_tree_merge": {
    "@fx-eval": "#phase0:tree.merge",
    fromPaths: [ ".a1.a2", ".b1.b2" ],
    ignoreFromPathsNotfound: false,
    toPath: ".res1.res2",
    body: {
      other_before: 1,
      a1: {
        a2: { 
          a2Field1: 123,
          a2Field2: 234, 
          sharedField1: "a-sharedField1", 
          sharedField2: 123  
        }  
      },
      b1: {
        b2: {  
          b2Field1: 123,
          b2Field2: 234, 
          sharedField1: "b-sharedField1", 
          sharedField2: 234  
        }  
      },
      res1: {
        res2: {}
      },
      other_after: 1
    }
  }
}
     </PRE>
     * => 
     * <PRE>
{
  "eval_tree_merge": {
    other_before: 1,
    a1: {
      a2: { 
        a2Field1: 123,
        a2Field2: 234, 
        sharedField1: "a-sharedField1", 
        sharedField2: 123  
      }  
    },
    b1: {
      b2: {  
        b2Field1: 123,
        b2Field2: 234, 
        sharedField1: "b-sharedField1", 
        sharedField2: 234  
      }  
    },
    res1: {
      res2: {
        a2Field1: 123,
        a2Field2: 234, 
        sharedField1: "b-sharedField1", 
        sharedField2: 234,
        b2Field1: 123,
        b2Field2: 234
      }
    },
    other_after: 1
  }
}
     * </PRE
     *
     */
    public static class FxMergeTreeFunc extends FxNodeFunc {
        public static final String NAME = "tree.merge";
        public static final FxMergeTreeFunc INSTANCE = new FxMergeTreeFunc();
        
        @Override
        public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            FxNode body = srcObj.get("body");
            FxArrayNode fromPathsArray = FxCurrEvalCtxUtil.recurseEvalToArray(ctx, srcObj.get("fromPaths"));
            FxNodePath toPath = FxCurrEvalCtxUtil.recurseEvalToPath(ctx, srcObj.get("toPath"));
            boolean ignoreNotFound = FxCurrEvalCtxUtil.recurseEvalToBooleanOrDefault(ctx, srcObj.get("ignoreFromPathsNotfound"), false);
            
            FxNode destBody = FxNodeCopyVisitor.copyTo(dest, body);

            FxNode destNode = toPath.select(destBody);

            FxNodeCopyMergeVisitor mergeVisitor = FxNodeCopyMergeVisitor.instance(true, false); 

            final int fromPathsLen = fromPathsArray.size();
            for (int i = 0; i < fromPathsLen; i++) {
                FxNode fromPathNode = fromPathsArray.get(i);
                FxNodePath fromPath = FxNodeValueUtils.nodeToPath(fromPathNode);
                
                FxNode fromNode = fromPath.select(body);
                if (fromNode == null) {
                    if (ignoreNotFound) {
                        throw new IllegalArgumentException("can not copy node, fromPath '" + fromPath + "' not found in body");
                    } else {
                        continue;
                    }
                }
    
                fromNode.accept(mergeVisitor, destNode);
            }
        }
    }
    
}
