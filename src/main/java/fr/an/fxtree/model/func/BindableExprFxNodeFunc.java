package fr.an.fxtree.model.func;

import java.util.function.Supplier;

import fr.an.fxtree.impl.helper.FxObjectMapper;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;

public class BindableExprFxNodeFunc<T extends FxBindedNodeFuncExpr> extends FxNodeFunc {

    private FxObjectMapper fxObjectMapper = new FxObjectMapper();

    private Supplier<T> exprFactory;
    
    // ------------------------------------------------------------------------

    public BindableExprFxNodeFunc(Class<T> exprClass, Supplier<T> exprFactory) {
        this.exprFactory = exprFactory;
    }

    // ------------------------------------------------------------------------

    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        T bind;
        if (src instanceof FxObjNode) {
            FxObjNode srcObj = (FxObjNode) src;
            bind = getBindOn(srcObj);
            if (bind == null) {
                bind = compileBind(src);
                putBindOn(srcObj, bind);
            }
        } else {
            // TODO?? compile + attach on array, property, text, .. using parent + suffix
            bind = compileBind(src);
        }
        
        bind.eval(dest);
    }
    
    @SuppressWarnings("unchecked")
    public T getBindOn(FxObjNode src) {
        FxPOJONode bindExprNode = src.get(FxConsts.FX_BINDED_EXPR);
        return bindExprNode != null? (T) bindExprNode.getValue() : null;
    }

    public void putBindOn(FxObjNode src, T bind) {
        FxSourceLoc loc = (bind.getSrc() != null)? bind.getSrc().getSourceLoc() : FxSourceLoc.inMem();  
        src.putPOJO(FxConsts.FX_BINDED_EXPR, bind, loc);
    }

    public T compileBind(FxNode src) {
        T res = exprFactory.get();
        res.setSrc(src); 
        // also extract params...
        if (src instanceof FxObjNode) {
            FxObjNode srcObj = (FxObjNode) src;
            FxObjNode paramsObj = srcObj.get(FxConsts.FX_PARAMS);
            if (paramsObj != null) {
                // inject deserialized params into <code>res</code> binded expr
                fxObjectMapper.readUpdate(srcObj, res);
                // TODO resolve path if any                
            }
        }
        
        return res;
    }
       
}
