package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;

public class FxJavaMethodInvokeFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper();
    private static final FxSourceLoc TST_loc = FxSourceLoc.inMem();

    @Test
    public void testEvalInvoke() {
        tstHelper.doTestFile("evalInvoke");
    }

    public static void testMethod_bool_char_int_long_float_double_dest_node_ctx(
            boolean boolValue, char charValue, int intValue, long longValue,
            float floatValue, double doubleValue,
            FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode destObj = dest.addObj(TST_loc);
        destObj.put("boolValue", boolValue, TST_loc);
        destObj.put("charValue", charValue, TST_loc);
        destObj.put("intValue", intValue, TST_loc);
        destObj.put("longValue", longValue, TST_loc);
        destObj.put("floatValue", floatValue, TST_loc);
        destObj.put("doubleValue", doubleValue, TST_loc);
        destObj.put("ctx-IsSet?", ctx!=null, TST_loc);
        destObj.put("src-IsSet?", src!=null, TST_loc);
    }

}
