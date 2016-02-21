package fr.an.fxtree.model.func;

import java.util.function.Supplier;

import fr.an.fxtree.impl.helper.FxObjectMapper;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;

public class BindableExprFxNodeFunc<T extends FxBindedNodeFuncExpr> extends FxNodeFunc {

    public static final String PROP_FX_BINDED_EXPR = "@fx-binded-expr";

    public static final String PROP_FX_PARAMS = "@fx-params";

    private FxObjectMapper fxObjectMapper = new FxObjectMapper();

    private Supplier<T> exprFactory;
    
    // ------------------------------------------------------------------------

    public BindableExprFxNodeFunc(Class<T> exprClass, Supplier<T> exprFactory) {
        this.exprFactory = exprFactory;
    }

    // ------------------------------------------------------------------------

    @Override
    public void eval(FxNode dest, FxNode src) {
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
        FxPOJONode bindExprNode = src.get(PROP_FX_BINDED_EXPR);
        return bindExprNode != null? (T) bindExprNode.getValue() : null;
    }

    public void putBindOn(FxObjNode src, T bind) {
        src.putPOJO(PROP_FX_BINDED_EXPR, bind);
    }

    public T compileBind(FxNode src) {
        T res = exprFactory.get();
        res.setSrc(src); 
        // also extract params...
        if (src instanceof FxObjNode) {
            FxObjNode srcObj = (FxObjNode) src;
            FxObjNode paramsObj = srcObj.get(PROP_FX_PARAMS);
            if (paramsObj != null) {
                // inject deserialized params into <code>res</code> binded expr
                fxObjectMapper.readUpdate(srcObj, res);
                // TODO resolve path if any                
            }
        }
        
        return res;
    }
       
}
