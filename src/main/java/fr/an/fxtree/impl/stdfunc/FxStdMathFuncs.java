package fr.an.fxtree.impl.stdfunc;

import fr.an.fxtree.impl.util.FxUtils;
import fr.an.fxtree.model.FxChildAdder;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxNumberType;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxNodeFunc;

public final class FxStdMathFuncs {

    public static class FxNodeSumFunc extends FxNodeFunc {
        public static final String NAME = "sum";
        public static final FxNodeSumFunc INSTANCE = new FxNodeSumFunc();
        private FxNodeSumFunc() {
        }
        
        @Override
        public FxNode eval(FxChildAdder out, FxNode src) {
            FxNode res = null;
            FxObjNode srcObj = (FxObjNode) src; 
            FxNode left = srcObj.get("left");
            FxNode right = srcObj.get("right");
            
            FxNumberType leftNumberType = left.numberType();
            FxNumberType rightNumberType = right.numberType();
            // deduce result type 
            FxNumberType resultNumberType = binaryOpType(leftNumberType, rightNumberType); 
            // compute with implicit coerce type...
            switch(resultNumberType) {
            case INT: {
                int leftInt = left.intValue();
                int rightInt = right.intValue();
                int result = leftInt + rightInt;  
                res = out.add(result);
            } break;
            // LONG, BIG_INTEGER, FLOAT, DOUBLE, BIG_DECIMAL
            default: throw FxUtils.notImplYet(); 
            }
            return res;
        }
        
    }
    
    
    // TODO refactor..
    protected static FxNumberType binaryOpType(FxNumberType left, FxNumberType right) {
        switch(left) {
        case INT:
            switch(right) {
            case INT: return FxNumberType.INT;
            case LONG: return FxNumberType.LONG;
            case BIG_INTEGER: return FxNumberType.BIG_INTEGER;
            case FLOAT: return FxNumberType.FLOAT;
            case DOUBLE: return FxNumberType.DOUBLE;
            case BIG_DECIMAL: return FxNumberType.BIG_DECIMAL;
            default: throw FxUtils.switchDefault(); 
            }
        case LONG:
            switch(right) {
            case INT: return FxNumberType.LONG;
            case LONG: return FxNumberType.LONG;
            case BIG_INTEGER: return FxNumberType.BIG_INTEGER;
            case FLOAT: return FxNumberType.FLOAT;
            case DOUBLE: return FxNumberType.DOUBLE;
            case BIG_DECIMAL: return FxNumberType.BIG_DECIMAL;
            default: throw FxUtils.switchDefault(); 
            }
        case BIG_INTEGER:
            switch(right) {
            case INT: return FxNumberType.BIG_INTEGER;
            case LONG: return FxNumberType.BIG_INTEGER;
            case BIG_INTEGER: return FxNumberType.BIG_INTEGER;
            case FLOAT: return FxNumberType.BIG_DECIMAL;
            case DOUBLE: return FxNumberType.BIG_DECIMAL;
            case BIG_DECIMAL: return FxNumberType.BIG_DECIMAL;
            default: throw FxUtils.switchDefault(); 
            }
        case FLOAT:
            switch(right) {
            case INT: return FxNumberType.FLOAT;
            case LONG: return FxNumberType.FLOAT;
            case BIG_INTEGER: return FxNumberType.BIG_DECIMAL;
            case FLOAT: return FxNumberType.FLOAT;
            case DOUBLE: return FxNumberType.DOUBLE;
            case BIG_DECIMAL: return FxNumberType.BIG_DECIMAL;
            default: throw FxUtils.switchDefault(); 
            }
        case DOUBLE:
            switch(right) {
            case INT: return FxNumberType.DOUBLE;
            case LONG: return FxNumberType.DOUBLE;
            case BIG_INTEGER: return FxNumberType.BIG_DECIMAL;
            case FLOAT: return FxNumberType.DOUBLE;
            case DOUBLE: return FxNumberType.DOUBLE;
            case BIG_DECIMAL: return FxNumberType.BIG_DECIMAL;
            }
        case BIG_DECIMAL:
            return FxNumberType.BIG_DECIMAL;
            
        default: throw FxUtils.switchDefault(); 
        }
    }
}
