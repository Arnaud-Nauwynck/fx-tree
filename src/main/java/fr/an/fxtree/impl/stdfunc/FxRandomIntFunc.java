package fr.an.fxtree.impl.stdfunc;

import java.util.Random;

import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxRandomIntFunc extends FxNodeFunc {

    public static final String NAME = "randomInt";
    
    // ------------------------------------------------------------------------

    public static final FxRandomIntFunc INSTANCE = new FxRandomIntFunc();
    
    private FxRandomIntFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        FxNode seedNode = (srcObj != null)? srcObj.get("seed") : null;
        long seed = (srcObj != null)? FxCurrEvalCtxUtil.recurseEvalToLongOrDefault(ctx, seedNode, 0L) : System.currentTimeMillis();
        FxNode minNode = (srcObj != null)? srcObj.get("min") : null;
        int minValue = (minNode != null)? FxCurrEvalCtxUtil.recurseEvalToInt(ctx, minNode) : 0;
        FxNode maxNode = (srcObj != null)? srcObj.get("max") : null;
        int maxValue = (maxNode != null)? FxCurrEvalCtxUtil.recurseEvalToInt(ctx, maxNode) : Integer.MAX_VALUE;
        
        Random rand = new Random(seed);
        int value = minValue + rand.nextInt(maxValue - minValue);

        dest.add(value);
    }
    
}
