package fr.an.fxtree.impl.stdfunc;

import org.junit.Test;

import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;

public class FxJavaMethodInvokeFuncTest {

    protected static FxEvalFuncTstHelper tstHelper = new FxEvalFuncTstHelper(); 

    @Test
    public void testEvalInvoke() {
        tstHelper.doTestFile("evalInvoke");
    }
    
    public static void testMethod_bool_char_int_long_float_double_dest_node_ctx(
            boolean boolValue, char charValue, int intValue, long longValue, 
            float floatValue, double doubleValue,
            FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode destObj = dest.addObj();
        destObj.put("boolValue", boolValue);
        destObj.put("charValue", charValue);
        destObj.put("intValue", intValue);
        destObj.put("longValue", longValue);
        destObj.put("floatValue", floatValue);
        destObj.put("doubleValue", doubleValue);
        destObj.put("ctx-IsSet?", ctx!=null);
        destObj.put("src-IsSet?", src!=null);
    }
    
}
