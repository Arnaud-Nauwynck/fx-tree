package fr.an.fxtree.impl.stdfunc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxLogVoidFunc extends FxNodeFunc {
    
    private static final Logger LOG = LoggerFactory.getLogger(FxLogVoidFunc.class);
    
    public static final String NAME = "log";
    
    // ------------------------------------------------------------------------

    public static final FxLogVoidFunc INSTANCE = new FxLogVoidFunc();
    
    private FxLogVoidFunc() {
    }

    // ------------------------------------------------------------------------
    
    @Override
    public FxNode eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        String message = FxNodeValueUtils.getStringOrThrow(srcObj, "message");
        String level =  FxNodeValueUtils.getOrDefault(srcObj, "level", "info");
        level = level.toLowerCase();
        
        switch (level) {
        case "trace":
            LOG.trace(message);
            break;
        case "debug":
            LOG.debug(message);
            break;
        case "info":
            LOG.info(message);
            break;
        case "warn":
        case "warning":
            LOG.warn(message);
            break;
        case "error":
            LOG.error(message);
            break;
        default:
            LOG.info(message);
            break;
        }
        
        return null;
    }

}
