package fr.an.fxtree.impl.stdfunc;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Throwables;

import fr.an.fxtree.impl.helper.FxNodeCopyVisitor;
import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxTryCatchFunc extends FxNodeFunc {

    public static final String NAME = "try-catch";
    
    // ------------------------------------------------------------------------

    public static final FxTryCatchFunc INSTANCE = new FxTryCatchFunc();
    
    private FxTryCatchFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        FxNode templateNode = FxNodeValueUtils.getOrThrow(srcObj, "body");
        FxNode catchNode = FxNodeValueUtils.getOrThrow(srcObj, "catch");
        
        FxSourceLoc loc = FxSourceLoc.newFrom(NAME, src.getSourceLoc());
        try {
            FxMemRootDocument tmpDoc = new FxMemRootDocument(src.getSourceLoc()); // loc..
            
            FxCurrEvalCtxUtil.recurseEvalTo(tmpDoc.contentWriter(), ctx, templateNode);
            
            // ok, no exception .. copy temp result
            FxNodeCopyVisitor.copyTo(dest, tmpDoc);
        } catch(Throwable ex) {
            FxMemRootDocument tmpDoc = new FxMemRootDocument(src.getSourceLoc());
            FxObjNode tmpExInfosObj = tmpDoc.contentWriter().addObj(loc);
            Map<String,FxNode> replExVars = new HashMap<>();
            
            putExNodes(replExVars, tmpExInfosObj, "ex", ex);
            Throwable cause = ex.getCause();
            if (cause == null) {
                cause = ex;
            }
            putExNodes(replExVars, tmpExInfosObj, "cause", cause);
            
            FxEvalContext childCtx = ctx.createChildContext();
            childCtx.putVariableAll(replExVars);
            FxVarsReplaceFunc replExVarsFunc = new FxVarsReplaceFunc(replExVars);
            
            replExVarsFunc.eval(dest, childCtx, catchNode);
        }
    }

    private void putExNodes(Map<String,FxNode> dest, 
            FxObjNode tmpExInfosObj, String varPrefix, Throwable ex) {
        String name; 
        // name = varPrefix; 
        // dest.put(name, tmpExInfosObj.putPOJO(name, ex));
        name = varPrefix + "Class";
        FxSourceLoc loc = tmpExInfosObj.getSourceLoc();
        dest.put(name, tmpExInfosObj.put(name, ex.getClass().getName(), loc));
        name = varPrefix + "Message";
        String message = ex.getMessage();
        if (message == null) {
            message = ""; //??
        }
        dest.put(name, tmpExInfosObj.put(name, message, loc));
        name = varPrefix + "StackTrace";
        String stackStr = Throwables.getStackTraceAsString(ex);
        dest.put(name, tmpExInfosObj.put(name, stackStr, loc));
    }
    
}
