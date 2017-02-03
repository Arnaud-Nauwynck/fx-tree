package fr.an.fxtree.impl.stdfunc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import fr.an.fxtree.impl.util.FxUtils;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxNumberType;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public final class FxStdMathFuncs {

    /** private to force all static */
    private FxStdMathFuncs() {}

    public static void registerBuiltinFuncs(Map<String, FxNodeFunc> dest) {
        registerBuiltinFuncs(dest,
            plusFunc(), minusFunc(), multFunc(), divideFunc()
            );
    }

    public static AbstractBinaryNumberOpFunc plusFunc() { return FxNodePlusBinaryNumberFunc.INSTANCE; }
    public static AbstractBinaryNumberOpFunc minusFunc() { return FxNodeMinusBinaryNumberFunc.INSTANCE; }
    public static AbstractBinaryNumberOpFunc multFunc() { return FxNodeMultBinaryNumberFunc.INSTANCE; }
    public static AbstractBinaryNumberOpFunc divideFunc() { return FxNodeDivideBinaryNumberFunc.INSTANCE; }

    // internal
    // ------------------------------------------------------------------------

    protected static void registerBuiltinFuncs(Map<String, FxNodeFunc> dest, AbstractBinaryNumberOpFunc... funcs) {
        for(AbstractBinaryNumberOpFunc func : funcs) {
            dest.put(func.name, func);
            if (func.alias != null) {
                dest.put(func.alias, func);
            }
        }
    }

    public static abstract class AbstractBinaryNumberOpFunc extends FxNodeFunc {
        private final String name;
        private final String alias;
        private final String displayOp;

        public AbstractBinaryNumberOpFunc(String name, String alias, String displayOp) {
            this.name = name;
            this.alias = alias;
            this.displayOp = displayOp;
        }

        protected abstract int evalBinaryOp(int lhs, int rhs);
        protected abstract long evalBinaryOp(long lhs, long rhs);
        protected abstract BigInteger evalBinaryOp(BigInteger lhs, BigInteger rhs);
        protected abstract float evalBinaryOp(float lhs, float rhs);
        protected abstract double evalBinaryOp(double lhs, double rhs);
        protected abstract BigDecimal evalBinaryOp(BigDecimal lhs, BigDecimal rhs);

        @Override
        public void eval(FxChildWriter out, FxEvalContext ctx, FxNode src) {
            FxObjNode srcObj = (FxObjNode) src;
            FxNode left = FxCurrEvalCtxUtil.recurseEval(ctx, srcObj.get("left"));
            FxNode right = FxCurrEvalCtxUtil.recurseEval(ctx, srcObj.get("right"));

            if (left == null || !left.isNumber()) {
                throw new IllegalArgumentException("expected 'left' number, got " + left.getNodeType());
            }
            if (right == null || !right.isNumber()) {
                throw new IllegalArgumentException("expected 'right' number, got " + right.getNodeType());
            }


            FxNumberType leftNumberType = left.numberType();
            FxNumberType rightNumberType = right.numberType();
            // deduce result type
            FxNumberType resultNumberType = binaryOpType(leftNumberType, rightNumberType);
            // compute with implicit coerce type...
            try {
                switch(resultNumberType) {
                case INT: {
                    int lhs = left.intValue();
                    int rhs = right.intValue();
                    int result = evalBinaryOp(lhs, rhs);
                    out.add(result);
                } break;
                case LONG: {
                    long lhs = left.longValue();
                    long rhs = right.longValue();
                    long result = evalBinaryOp(lhs, rhs);
                    out.add(result);
                } break;
                case BIG_INTEGER: {
                    BigInteger lhs = left.bigIntegerValue();
                    BigInteger rhs = right.bigIntegerValue();
                    BigInteger result = evalBinaryOp(lhs, rhs);
                    out.add(result);
                } break;
                case FLOAT: {
                    float lhs = left.floatValue();
                    float rhs = right.floatValue();
                    float result = evalBinaryOp(lhs, rhs);
                    out.add(result);
                } break;
                case DOUBLE: {
                    double lhs = left.doubleValue();
                    double rhs = right.doubleValue();
                    double result = evalBinaryOp(lhs, rhs);
                    out.add(result);
                } break;
                case BIG_DECIMAL: {
                    BigDecimal lhs = left.decimalValue();
                    BigDecimal rhs = right.decimalValue();
                    BigDecimal result = evalBinaryOp(lhs, rhs);
                    out.add(result);
                } break;
                default: throw FxUtils.switchDefault();
                }
            } catch(ArithmeticException ex) {
                out.add("@ERROR ArithmeticException");
            }
        }

        @Override
        public String toString() {
            return "FxFunc[" + name + ", op:'" + displayOp + "']";
        }

    }


    public static final class FxNodePlusBinaryNumberFunc extends AbstractBinaryNumberOpFunc {
        public static final String NAME = "num.add";
        public static final FxNodePlusBinaryNumberFunc INSTANCE = new FxNodePlusBinaryNumberFunc();
        private FxNodePlusBinaryNumberFunc() {
            super(NAME, "plus", "+");
        }

        @Override
		protected int evalBinaryOp(int lhs, int rhs) {
            return lhs + rhs;
        }
        @Override
		protected long evalBinaryOp(long lhs, long rhs) {
            return lhs + rhs;
        }
        @Override
		protected BigInteger evalBinaryOp(BigInteger lhs, BigInteger rhs) {
            return lhs.add(rhs);
        }
        @Override
		protected float evalBinaryOp(float lhs, float rhs) {
            return lhs + rhs;
        }
        @Override
		protected double evalBinaryOp(double lhs, double rhs) {
            return lhs + rhs;
        }
        @Override
		protected BigDecimal evalBinaryOp(BigDecimal lhs, BigDecimal rhs) {
            return lhs.add(rhs);
        }

    }


    public static final class FxNodeMinusBinaryNumberFunc extends AbstractBinaryNumberOpFunc {
        public static final String NAME = "num.substract";
        public static final FxNodeMinusBinaryNumberFunc INSTANCE = new FxNodeMinusBinaryNumberFunc();
        private FxNodeMinusBinaryNumberFunc() {
            super(NAME, "minus", "-");
        }

        @Override
		protected int evalBinaryOp(int lhs, int rhs) {
            return lhs - rhs;
        }
        @Override
		protected long evalBinaryOp(long lhs, long rhs) {
            return lhs - rhs;
        }
        @Override
		protected BigInteger evalBinaryOp(BigInteger lhs, BigInteger rhs) {
            return lhs.subtract(rhs);
        }
        @Override
		protected float evalBinaryOp(float lhs, float rhs) {
            return lhs - rhs;
        }
        @Override
		protected double evalBinaryOp(double lhs, double rhs) {
            return lhs - rhs;
        }
        @Override
		protected BigDecimal evalBinaryOp(BigDecimal lhs, BigDecimal rhs) {
            return lhs.subtract(rhs);
        }

    }


    public static final class FxNodeMultBinaryNumberFunc extends AbstractBinaryNumberOpFunc {
        public static final String NAME = "num.multiply";
        public static final FxNodeMultBinaryNumberFunc INSTANCE = new FxNodeMultBinaryNumberFunc();
        private FxNodeMultBinaryNumberFunc() {
            super(NAME, "mult", "*");
        }

        @Override
		protected int evalBinaryOp(int lhs, int rhs) {
            return lhs * rhs;
        }
        @Override
		protected long evalBinaryOp(long lhs, long rhs) {
            return lhs * rhs;
        }
        @Override
		protected BigInteger evalBinaryOp(BigInteger lhs, BigInteger rhs) {
            return lhs.multiply(rhs);
        }
        @Override
		protected float evalBinaryOp(float lhs, float rhs) {
            return lhs * rhs;
        }
        @Override
		protected double evalBinaryOp(double lhs, double rhs) {
            return lhs * rhs;
        }
        @Override
		protected BigDecimal evalBinaryOp(BigDecimal lhs, BigDecimal rhs) {
            return lhs.multiply(rhs);
        }

    }

    public static final class FxNodeDivideBinaryNumberFunc extends AbstractBinaryNumberOpFunc {
        public static final String NAME = "num.divide";
        public static final FxNodeDivideBinaryNumberFunc INSTANCE = new FxNodeDivideBinaryNumberFunc();
        private FxNodeDivideBinaryNumberFunc() {
            super(NAME, "div", "/");
        }

        @Override
		protected int evalBinaryOp(int lhs, int rhs) {
            if (rhs == 0) throw new ArithmeticException();
            return lhs / rhs;
        }
        @Override
		protected long evalBinaryOp(long lhs, long rhs) {
            if (rhs == 0) throw new ArithmeticException();
            return lhs / rhs;
        }
        @Override
		protected BigInteger evalBinaryOp(BigInteger lhs, BigInteger rhs) {
            return lhs.divide(rhs);
        }
        @Override
		protected float evalBinaryOp(float lhs, float rhs) {
            if (rhs == 0.0) throw new ArithmeticException();
            return lhs / rhs;
        }
        @Override
		protected double evalBinaryOp(double lhs, double rhs) {
            if (rhs == 0.0) throw new ArithmeticException();
            return lhs / rhs;
        }
        @Override
		protected BigDecimal evalBinaryOp(BigDecimal lhs, BigDecimal rhs) {
            return lhs.divide(rhs);
        }

    }

    // internal
    // ------------------------------------------------------------------------


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
