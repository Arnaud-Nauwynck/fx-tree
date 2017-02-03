package fr.an.fxtree.impl.stdfunc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxLogVoidFunc extends FxNodeFunc {

    public static final String NAME = "log";

    private Logger logger;

    // ------------------------------------------------------------------------

    public static final FxLogVoidFunc INSTANCE = new FxLogVoidFunc();

    private FxLogVoidFunc() {
        this(LoggerFactory.getLogger(FxLogVoidFunc.class));
    }

    private FxLogVoidFunc(Logger logger) {
        this.logger = logger;
    }

    // ------------------------------------------------------------------------

    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        String message = FxCurrEvalCtxUtil.recurseEvalToString(ctx, srcObj.get("message"));
        String level =  FxCurrEvalCtxUtil.recurseEvalToStringOrDefault(ctx, srcObj.get("level"), "info");
        level = level.toLowerCase();

        switch (level) {
        case "trace":
            logger.trace(message);
            break;
        case "debug":
            logger.debug(message);
            break;
        case "info":
            logger.info(message);
            break;
        case "warn":
        case "warning":
            logger.warn(message);
            break;
        case "error":
            logger.error(message);
            break;
        default:
            logger.info(message);
            break;
        }
    }

}
